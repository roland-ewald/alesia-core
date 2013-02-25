package alesia.planning

import alesia.utils.bdd.UniqueTable
import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.List
import sessl.util.Logging

/**
 * Represents a general planning domain.
 *
 * @author Roland Ewald
 */
class PlanningDomain extends Logging {

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
   * Helper method for debugging.
   * @param f boolean function to show
   * @param name name of the function to display
   */
  def debug(f: Int, name: String) = logger.debug(name + "(#" + f + "):\n" + table.structureOf(f, variableNames).mkString("\n"))

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
  def createFrameAxioms(affectedNextStateVars: List[Int]): List[Int] = {
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
    import t._
    lazy val addNextState = add.map(t.substitute(_, nextStateVarNums))
    lazy val delNextState = del.map(t.substitute(_, nextStateVarNums))
    lazy val effectConjunction = add.map(_.id) ::: del.map(f => not(f.id))
    lazy val currentStateEffectVariables = variablesOf(effectConjunction: _*).filter(currentStateVarNums.contains)
    lazy val addVars = variablesOf(add.map(_.id): _*).toSet
    lazy val delVars = variablesOf(del.map(_.id): _*).toSet
    lazy val changes = addVars ++ delVars
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

    override def strongPreImage(currentState: Int) = {
      val nextState = forwardShift(currentState) //Q(x')
      val transitionAndNextState = and(strongPreImgStateTransition, nextState) // R(x_i,x'_i)∧(Q(x'))
      exists(nextStateVariables(transitionAndNextState), transitionAndNextState) //exists x_i': R(x_i,x'_i)∧(Q(x'))
    }

    override def weakPreImage(currentState: Int) =
      calculateImage(currentState, weakPreImageTransition, preConWeakPreImgTransVars)

    def calculateImage(currentState: Int, actionTransition: Int, actionTransitionVars: List[Int]): Int = {
      val nextState = forwardShift(currentState) //Q(x')
      exists((nextStateVariables(nextState) ++ actionTransitionVars).distinct, and(nextState, actionTransition)) //exists x_i': Q(x')∧R(x_i,x'_i)
    }

    lazy val nextStateEffectVars = effects.flatMap(_.currentStateEffectVariables).toSet

    /** ξ(a) / the relation R(s, this, s')*/
    lazy val strongPreImgStateTransition: Int = {

      def preImgEffect(e: Effect): Int = {
        implies(e.condition.id, (e.addNextState ::: e.delNextState.map(not)).foldLeft(1)(and))
      }

      val detEffect = effects.filter(!_.nondeterministic).map(preImgEffect).foldLeft(precondition.id)(and)
      effects.filter(_.nondeterministic).map(preImgEffect).map(and(_, detEffect)).foldLeft(detEffect)(or)
    }

    /** Get all x' defined in the effects and the state-transition conjunction.*/
    def nextStateVariables(stateTransition: Int): List[Int] = {
      varsOf(stateTransition).filter(x => (currentStateVarNums.contains(x) && !nextStateEffectVars.contains(x))) ++ nextStateEffectVars
    }

    lazy val preConWeakPreImgTransVars = varsOf(weakPreImageTransition).filter(currentStateVarNums.contains)

    lazy val weakPreImageTransition = and(precondition, weakPreImageTransitionWithoutPrec)

    lazy val weakPreImageTransitionWithoutPrec: Int = {

      val ndEffects = effects.filter(_.nondeterministic)
      val dEffects = effects.filter(!_.nondeterministic)
      val allVarsCurrentState = nextStateVarNums.keySet

      def epc(e: Effect, varNum: Int, positive: Boolean): Int = {
        if ((positive && e.addVars.contains(varNum)) ||
          (!positive && e.delVars.contains(varNum)))
          e.condition
        else FalseVariable
      }

      def plEffect(e: Effect, vars: Iterable[Int]): Int = {
        vars.map(v => iff(
          varNumInstructionIds(nextStateVarNums(v)), //v is true in next state iff:
          or(
            and(
              varNumInstructionIds(v),
              not(epc(e, v, false))),
            epc(e, v, true)))).foldLeft(TrueVariable.id)(and)
      }

      def plConjunction(vars: Iterable[Int], es: Seq[Effect]): Int = {
        if (es.isEmpty) {
          FalseVariable.id
        } else {
          val furtherChanges = es.tail.map(_.changes)
          val allFurtherChanges = furtherChanges.flatten.toSet
          val varsFirstEffect = vars.filter(!allFurtherChanges.contains(_))
          es.tail.zip(furtherChanges).foldLeft(plEffect(es.head, varsFirstEffect))((x, eff) => and(x, plEffect(eff._1, eff._2)))
        }
      }

      if (ndEffects.isEmpty)
        plConjunction(allVarsCurrentState, dEffects)
      else
        ndEffects.map(e => plConjunction(allVarsCurrentState, dEffects :+ e)).foldLeft(plConjunction(allVarsCurrentState, dEffects))(or)
    }
  }
}