package alesia.planning

import alesia.utils.bdd.UniqueTable

/**
 * Represents a general planning domain.
 *
 * @author Roland Ewald
 */
class PlanningDomain {

  /** The table to manage the boolean functions. */
  private[this] val table = new UniqueTable

  /** Maps instruction ids for the boolean functions f(x) = x to the variable x's name in the domain. */
  val varNames = scala.collection.mutable.Map[Int, String]()

  /** Contains actions available in this domain. */
  private[this] var actions = IndexedSeq[DomainAction]()

  /**
   * Creates a new variable. Names does not need to be unique, but the id of the [[PlanningDomainVariable]] will be.
   * @param name does not need to be unique
   * @return domain variable
   */
  def v(name: String): PlanningDomainVariable = {
    val instrId = table.unique(table.variableCount + 1, 0, 1)
    varNames(instrId) = name
    new PlanningDomainVariable(instrId, name)
  }

  /**
   * Creates a new action.
   * @param name does not need to be unique
   * @param precondition the precondition
   * @param effects both deterministic and nondeterministic effects
   * @return action
   */
  def action(name: String, precondition: PlanningDomainVariable, effects: Effect*): DomainAction = {
    val rv = new DomainAction(name, precondition, effects: _*)
    actions = actions :+ rv
    rv
  }

  /** @return the number of variables defined in the domain */
  def numVariables = varNames.size

  /** @return the number of boolean functions defined in the domain */
  def numFunctions = table.instructionCount

  /** Creates variable by id and name. */
  private def createVarById(id: Int, name: String = "unknown") = PlanningDomainVariable(id, varNames.getOrElseUpdate(id, name))

  /** Facilitates usage of variables in code that relies on instruction ids only.*/
  implicit def variableToInstructionId(v: PlanningDomainVariable): Int = v.id

  /** Represents a domain variable, or a function of these. */
  case class PlanningDomainVariable(id: Int, name: String) {

    /** Creates readable name (useful for debugging).*/
    def createName(otherName: String, operator: Char) = '(' + name + ')' + operator + '(' + otherName + ')'

    //Pass all operators to the table
    def or(v: PlanningDomainVariable) = createVarById(table.or(id, v.id), createName(v.name, '∨'))
    def and(v: PlanningDomainVariable) = createVarById(table.and(id, v.id), createName(v.name, '∧'))
    def xor(v: PlanningDomainVariable) = createVarById(table.xor(id, v.id), createName(v.name, '⊕'))
    def unary_! = createVarById(table.not(id), "¬(" + name + ')')
  }

  /** The trivial domain variable for 'false'. */
  object FalseVariable extends PlanningDomainVariable(0, "false")

  /** The trivial domain variable for 'true'. */
  object TrueVariable extends PlanningDomainVariable(1, "true")

  /** Represents an effect of an action. */
  case class Effect(condition: PlanningDomainVariable = TrueVariable, add: List[PlanningDomainVariable] = List(), del: List[PlanningDomainVariable] = List(), nondeterministic: Boolean = false)

  /** Supplies helper functions to create effects. */
  object Effect {
    def apply(oldState: PlanningDomainVariable, newState: PlanningDomainVariable) = new Effect(oldState, add = List(newState), del = List(oldState))
  }

  /**
   * Represents a domain action.
   * @param name the name of the action, does not need to be unique
   * @param precondition the precondition of the action
   * @param effects the effects of the action
   */
  case class DomainAction(name: String, precondition: PlanningDomainVariable, effects: Effect*)
}