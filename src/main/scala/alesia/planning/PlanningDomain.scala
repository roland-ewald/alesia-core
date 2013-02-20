package alesia.planning

import alesia.utils.bdd.UniqueTable
import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.List

/**
 * Represents a general planning domain.
 *
 * @author Roland Ewald
 */
class PlanningDomain {

  /** The table to manage the boolean functions. */
  private[alesia] implicit val table = new UniqueTable

  /** Maps variable numbers to variable names.*/
  val variableNames = scala.collection.mutable.Map[Int, String]()

  /** Maps variable function instruction ids to their corresponding next-state variables. */
  val nextStateVars = scala.collection.mutable.Map[Int, PlanningDomainFunction]()

  /** Maps current-state variable numbers to their corresponding next-state variable numbers (for substitution). */
  val nextStateVarNums = scala.collection.mutable.Map[Int, Int]()

  /** Maps next-state variable function instruction ids to their corresponding current-state variables. */
  val currentStateVars = scala.collection.mutable.Map[Int, PlanningDomainFunction]()

  /** Maps next-state variable numbers to their corresponding current-state variable numbers (for substitution). */
  val currentStateVarNums = scala.collection.mutable.Map[Int, Int]()

  /** Maps variable numbers to the instruction ids of their indicator functions.*/
  val varNumInstructionIds = scala.collection.mutable.Map[Int, Int]()

  /** Buffers actions available in this domain. */
  private[this] var actionBuffer = ArrayBuffer[DomainAction]()

  /** All actions defined in this domain. */
  lazy val actions = actionBuffer.toArray

  /**
   * Allows to define a variable v. Internally, two variables named v and v' will be created,
   * representing the value of v for the current and the next state.
   */
  def v(name: String): PlanningDomainFunction =
    synchronized {
      //TODO: Once substitution works for general mappings, there is no need for synchronization anymore
      val (currentStateVarNum, currentStateVar) = createVariable(name)
      val (nextStateVarNum, nextStateVar) = createVariable(name + "'")

      nextStateVars(currentStateVar) = nextStateVar
      varNumInstructionIds(currentStateVarNum) = currentStateVar
      nextStateVarNums(currentStateVarNum) = nextStateVarNum

      currentStateVars(nextStateVar) = currentStateVar
      varNumInstructionIds(nextStateVarNum) = nextStateVar
      currentStateVarNums(nextStateVarNum) = currentStateVarNum

      currentStateVar
    }

  /**
   * Creates a function f(x) = x for a new variable x.
   * The name does not need to be unique, but the id of the function will be.
   * @param name, does not need to be unique
   * @return (variable number, domain variable)
   */
  private[this] def createVariable(name: String): (Int, PlanningDomainFunction) = {
    val variableNumber = table.variableCount + 1
    variableNames(variableNumber) = name
    (variableNumber, new PlanningDomainFunction(table.unique(variableNumber, 0, 1), name))
  }

  /**
   * Creates a new action.
   * @param name does not need to be unique
   * @param precondition the precondition
   * @param effects both deterministic and nondeterministic effects
   * @return action
   */
  def action(name: String, precondition: PlanningDomainFunction, effects: Effect*): DomainAction = {
    val rv = new DomainAction(name, precondition, effects: _*)
    actionBuffer += rv
    rv
  }

  /** @return the number of boolean functions defined in the domain */
  def numFunctions = table.instructionCount

  /** @return the number of available actions */
  def numActions = actionBuffer.size

  /** Facilitates usage of functions in code that relies on instruction ids only.*/
  implicit def variableToInstructionId(f: PlanningDomainFunction): Int = f.id

  /** Replaces all references to 'current state'-variables with their corresponding 'next state'-variables. f[x/x'].  */
  def forwardShift(f: Int) = table.substitute(f, nextStateVarNums)

  /** Replaces all references to 'next state'-variables with their corresponding 'current state'-variables. f[x'/x].  */
  def backwardShift(f: Int) = table.substitute(f, currentStateVarNums)

  /**
   * Generates a list of 'frame problem' axioms given the number of next-state variable that are unaffected.
   * @param the list of affected next-state variables V'_a
   * @return list of instruction ids representing (v_1<=>v_1'), ..., (v_n<=>v_n') with v_1, ..., v_n not in V'_a
   */
  def frameAxioms(affectedNextStateVars: List[Int]): List[Int] = {
    val unaffectedNextStateVars = (currentStateVarNums.keySet -- affectedNextStateVars.toSet).toList
    unaffectedNextStateVars.map(v => table.iff(varNumInstructionIds(v), varNumInstructionIds(currentStateVarNums(v))))
  }

  /** Represents a boolean function within the domain. */
  case class PlanningDomainFunction(id: Int, name: String) {

    /** Creates readable name (useful for debugging).*/
    def createName(otherName: String, operator: Char) = '(' + name + ')' + operator + '(' + otherName + ')'

    //Pass all operators to the table
    def or(f: PlanningDomainFunction) = PlanningDomainFunction(table.or(id, f.id), createName(f.name, '∨'))
    def and(f: PlanningDomainFunction) = PlanningDomainFunction(table.and(id, f.id), createName(f.name, '∧'))
    def xor(f: PlanningDomainFunction) = PlanningDomainFunction(table.xor(id, f.id), createName(f.name, '⊕'))
    def unary_! = PlanningDomainFunction(table.not(id), "¬(" + name + ')')
  }

  /** The trivial constant domain function () -> false. */
  object FalseVariable extends PlanningDomainFunction(0, "false")

  /** The trivial constant domain function () -> true. */
  object TrueVariable extends PlanningDomainFunction(1, "true")

  /** Represents an effect of an action. */
  case class Effect(condition: PlanningDomainFunction = TrueVariable, add: List[PlanningDomainFunction] = List(), del: List[PlanningDomainFunction] = List(), nondeterministic: Boolean = false)(implicit t: UniqueTable) {
    val addNextState = add.map(t.substitute(_, nextStateVarNums))
    val delNextState = del.map(t.substitute(_, nextStateVarNums))
  }

  /** Supplies helper functions to create effects. */
  object Effect {
    def apply(oldState: PlanningDomainFunction, newState: PlanningDomainFunction) = new Effect(oldState, add = List(newState), del = List(oldState))
  }

  /**
   * Represents a domain action.
   * @param name the name of the action, does not need to be unique
   * @param precondition the precondition of the action
   * @param effects the effects of the action
   */
  case class DomainAction(name: String, precondition: PlanningDomainFunction, effects: Effect*)(implicit table: UniqueTable) extends PlanningDomainAction {

    /** The action is only valid in the given planning domain. */
    private[this] val t = table

    import t._

    /**
     * Defines the pre-image for a given effect. TODO:move to effect
     */
    private[this] def preImgEffect(e: Effect): Int = {
      val involvedNextStateVars = variablesOf((e.addNextState ::: e.delNextState): _*)
      implies(e.condition.id,
        (e.addNextState ::: e.delNextState.map(not(_)) ::: frameAxioms(involvedNextStateVars)).foldLeft(1)(and))
    }

    /** ξ(a) / the relation R(s, this, s')*/
    lazy val strongPreImgStateTransition: Int = {
      val detEffect = effects.filter(!_.nondeterministic).map(preImgEffect).foldLeft(precondition.id)(and)
      effects.filter(_.nondeterministic).map(preImgEffect).map(and(_, detEffect)).foldLeft(detEffect)(or)
    }

    /** Get all x' defined in the effects and the state-transition conjunction.*/
    def nextStateVariables(stateTransition: Int) =
      variablesOf(effects.flatMap(effectConj) :+ stateTransition: _*).filter(currentStateVarNums.contains)

    def effectConj(e: Effect) = e.add.map(_.id) ::: e.del.map(f => not(f.id))

    /**
     * Returns the set of states from which the current state can be reached by this action.
     * @param currentState the instruction id of the current set of states
     * @return the instruction id of the set of states from which this set can be reached
     */
    override def strongPreImage(currentState: Int) = {
      val nextState = forwardShift(currentState) //Q(x')
      val transitionAndNextState = and(strongPreImgStateTransition, nextState) // R(x_i,x'_i)∧(Q(x'))
      val xPrime = nextStateVariables(transitionAndNextState) //x'
      exists(xPrime, transitionAndNextState) //exists x_i': R(x_i,x'_i)∧(Q(x')
    }

    override def weakPreImage(currentState: Int) = {
      val detEffect = effects.filter(!_.nondeterministic).map(preImgEffect).foldLeft(precondition.id)(and)
      val detEffectCurrentState = and(currentState, detEffect)
      val weakPreImgStateTransition = effects.filter(_.nondeterministic).map(preImgEffect).map(and(_, detEffectCurrentState)).foldLeft(detEffectCurrentState)(or)
      exists(nextStateVariables(weakPreImgStateTransition), weakPreImgStateTransition) //exists x_i': R(x_i,x'_i)
    }
    
  }
}