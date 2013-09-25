package alesia.planning

import scala.language.implicitConversions

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

  /** Returns a conjunction of all given functions, and false (empty set) when empty.*/
  def conjunction(fs: Iterable[PlanningDomainFunction]): PlanningDomainFunction = fs.headOption match {
    case None => FalseVariable
    case Some(x) => fs.reduce(_ and _)
  }

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
  case class Effect(condition: PlanningDomainFunction = TrueVariable, add: Seq[PlanningDomainFunction] = Seq(), del: Seq[PlanningDomainFunction] = Seq(), nondeterministic: Boolean = false)(implicit t: UniqueTable) {
    import t._
    lazy val addNextState = add.map(t.substitute(_, nextStateVarNums))
    lazy val delNextState = del.map(t.substitute(_, nextStateVarNums))
    lazy val effectConjunction = add.map(_.id) ++ del.map(f => not(f.id))
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
   *
   * @param name the name of the action, does not need to be unique
   * @param precondition the precondition of the action
   * @param effects the effects of the action
   */
  case class DomainAction(name: String, precondition: PlanningDomainFunction, effects: Effect*)(implicit table: UniqueTable) extends PlanningDomainAction {

    /** The action is only valid in the given planning domain. */
    private[this] val t = table

    import t._

    override def strongPreImage(currentState: Int) =
      calculatePreImage(currentState, strongPreImageStateTransition, strongPreImageStateTransitionVars, strongPreImageCache)

    override def weakPreImage(currentState: Int) =
      calculatePreImage(currentState, weakPreImageStateTransition, weakPreImageStateTransitionVars, weakPreImageCache)

    /**
     * Defines a boolean function for a pre-image. It has the form exists x_i': Q(x')∧R(x_i,x'_i).
     * @param currentState the current state Q(x) [will be shifted forward]
     * @param actionTransition the relation between states before and after action application, R(x,x')
     * @param actionTransitionVars the action variables that need to be quantified (additionally, those from Q(x))
     * @param cache the cache to store the results in
     */
    def calculatePreImage(currentState: Int, actionTransition: Int, actionTransitionVars: List[Int], cache: scala.collection.mutable.Map[Int, Int]): Int = {
      val nextState = forwardShift(currentState) //Q(x')
      cache.getOrElseUpdate(currentState,
        // exists x_i': Q(x')∧R(x_i,x'_i)
        exists((nextStateVariables(nextState) ++ actionTransitionVars).distinct, and(nextState, actionTransition)))
    }

    lazy val nextStateEffectVars = effects.flatMap(_.currentStateEffectVariables).toSet

    /** Get all x' defined in the effects and the state-transition conjunction.*/
    def nextStateVariables(stateTransition: Int): List[Int] = {
      varsOf(stateTransition).filter(x => (currentStateVarNums.contains(x) && !nextStateEffectVars.contains(x))) ++ nextStateEffectVars
    }

    //Strong pre-image computation:

    lazy val strongPreImageCache = scala.collection.mutable.Map[Int, Int]()

    lazy val strongPreImageStateTransitionVars = nextStateVars(strongPreImageStateTransition)

    /** The transition relation R(x, x') for this action. */
    lazy val strongPreImageStateTransition: Int = {

      def preImgEffect(e: Effect): Int = {
        implies(e.condition.id, (e.addNextState ++ e.delNextState.map(not)).foldLeft(1)(and))
      }

      val detEffect = effects.filter(!_.nondeterministic).map(preImgEffect).foldLeft(precondition.id)(and)
      effects.filter(_.nondeterministic).map(preImgEffect).map(and(_, detEffect)).foldLeft(detEffect)(or)
    }

    //Weak pre-image computation:

    lazy val weakPreImageCache = scala.collection.mutable.Map[Int, Int]()

    lazy val weakPreImageStateTransitionVars = nextStateVars(weakPreImageStateTransition)

    def nextStateVars(f: Int) = varsOf(f).filter(currentStateVarNums.contains)

    lazy val weakPreImageStateTransition = and(precondition, weakPreImageTransitionWithoutPrec)

    lazy val weakPreImageTransitionWithoutPrec: Int = {

      val ndEffects = effects.filter(_.nondeterministic)
      val dEffects = effects.filter(!_.nondeterministic)
      val allVarsCurrentState = nextStateVarNums.keySet

      if (ndEffects.isEmpty) {
        detEffects(allVarsCurrentState, dEffects)
      } else
        ndEffects.map(e => detEffects(allVarsCurrentState, dEffects :+ e)).foldLeft(detEffects(allVarsCurrentState, dEffects))(or)
    }

    def detEffects(vars: Iterable[Int], es: Seq[Effect]): Int = {
      vars.map(v => {
        iff(
          varNumInstructionIds(nextStateVarNums(v)), //v' is true (i.e. v in next state) iff:
          or(
            and( //v is true in current state and not made false... 
              varNumInstructionIds(v),
              not(epc(es, v, false))),
            epc(es, v, true))) //... or it is made true            
        //        debug(criterion, variableNames(nextStateVarNums(v)) + "<=> ")
      }).foldLeft(TrueVariable.id)(and)
    }

    def epc(e: Seq[Effect], varNum: Int, positive: Boolean): Int = {
      e.foldLeft(0)((x, eff) => or(x, epc(eff, varNum, positive)))
      // debug(result, "EPC for " + (if (positive) "" else "!") + variableNames(varNum))
    }

    def epc(e: Effect, varNum: Int, positive: Boolean): Int = {
      if ((positive && e.addVars.contains(varNum)) ||
        (!positive && e.delVars.contains(varNum)))
        e.condition
      else FalseVariable
    }

    @deprecated("Conjunction of non-deterministic effects not required here", "2/2013")
    def plConjunctionNonDetEffects(vars: Iterable[Int], es: Seq[Effect]): Int = {
      if (es.isEmpty) {
        FalseVariable.id
      } else {
        val furtherChanges = es.tail.map(_.changes)
        val allFurtherChanges = furtherChanges.flatten.toSet
        val varsFirstEffect = vars.filter(!allFurtherChanges.contains(_))
        es.tail.zip(furtherChanges).foldLeft(detEffects(varsFirstEffect, Seq(es.head)))((x, eff) => and(x, detEffects(eff._2, Seq(eff._1))))
      }
    }
  }
}