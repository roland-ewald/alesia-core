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
  var actions = IndexedSeq[DomainAction]()

  /**
   * Creates a new variable. Names does not need to be unique, but the id of the [[DomainVariable]] will be.
   * @param name does not need to be unique
   * @return domain variable
   */
  def v(name: String): DomainVariable = {
    val instrId = table.unique(table.variableCount + 1, 0, 1)
    varNames(instrId) = name
    new DomainVariable(instrId, name)
  }

  /**
   * Creates a new action.
   * @param name does not need to be unique
   * @param precondition the precondition
   * @param effects both deterministic and nondeterministic effects
   * @return action
   */
  def action(name: String, precondition: DomainVariable, effects: Effect*): DomainAction = {
    val rv = new DomainAction(name, precondition, effects: _*)
    actions = actions :+ rv
    rv
  }

  /** @return the number of variables defined in the domain */
  def numVariables = varNames.size

  /** @return the number of boolean functions defined in the domain */
  def numFunctions = table.instructionCount

  /** Creates variable by id and name. */
  private def createVarById(id: Int, name: String = "unknown") = DomainVariable(id, varNames.getOrElseUpdate(id, name))

  /** Represents a domain variable, or a function of these. */
  case class DomainVariable(id: Int, name: String) {
    def createName(otherName: String, operator: Char) = '(' + name + ')' + operator + '(' + otherName + ')'
    //Pass all operators to the table
    def or(v: DomainVariable) = createVarById(table.or(id, v.id), createName(v.name, '∨'))
    def and(v: DomainVariable) = createVarById(table.and(id, v.id), createName(v.name, '∧'))
    def xor(v: DomainVariable) = createVarById(table.xor(id, v.id), createName(v.name, '⊕'))
    def unary_! = createVarById(table.not(id), "¬(" + name + ')')
  }

  /** The trivial domain variable for 'false'. */
  object FalseVariable extends DomainVariable(0, "false")

  /** The trivial domain variable for 'true'. */
  object TrueVariable extends DomainVariable(1, "true")

  /** Represents an effect of an action. */
  case class Effect(condition: DomainVariable = TrueVariable, add: List[DomainVariable] = List(), del: List[DomainVariable] = List(), nondeterministic: Boolean = false)

  /** Supplies helper functions to create effects. */
  object Effect {
    def apply(oldState: DomainVariable, newState: DomainVariable) = new Effect(oldState, add = List(newState), del = List(oldState))
  }

  /**
   * Represents a domain action.
   * @param name the name of the action, does not need to be unique
   * @param precondition the precondition of the action
   * @param effects the effects of the action
   */
  case class DomainAction(name: String, precondition: DomainVariable, effects: Effect*)
}