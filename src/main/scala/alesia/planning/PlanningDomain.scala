package alesia.planning

import alesia.utils.bdd.UniqueTable
import scala.collection.mutable.ArrayBuffer

/**
 * Represents a general planning domain.
 *
 * @author Roland Ewald
 */
class PlanningDomain {

  /** The table to manage the boolean functions. */
  protected[alesia] val table = new UniqueTable

  /** Maps instruction ids for the boolean functions f(x) = x to their descriptions in the domain (mostly for debugging purposes). */
  val descriptions = scala.collection.mutable.Map[Int, String]()

  /** Buffers actions available in this domain. */
  private[this] var actionBuffer = ArrayBuffer[DomainAction]()

  /** All actions defined in this domain. */
  lazy val actions = actionBuffer.toArray

  /**
   * Creates a function f(x) = x for a new variable x.
   * The name does not need to be unique, but the id of the function will be.
   * @param name, does not need to be unique
   * @return domain variable
   */
  def v(name: String): PlanningDomainFunction = {
    val instrId = table.unique(table.variableCount + 1, 0, 1)
    descriptions(instrId) = name
    new PlanningDomainFunction(instrId, name)
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

  /** Creates variable by id and name. */
  private def createVarById(id: Int, name: String = "unknown") = PlanningDomainFunction(id, descriptions.getOrElseUpdate(id, name))

  /** Facilitates usage of functions in code that relies on instruction ids only.*/
  implicit def variableToInstructionId(f: PlanningDomainFunction): Int = f.id

  /** Represents a boolean function within the domain. */
  case class PlanningDomainFunction(id: Int, name: String) {

    /** Creates readable name (useful for debugging).*/
    def createName(otherName: String, operator: Char) = '(' + name + ')' + operator + '(' + otherName + ')'

    //Pass all operators to the table
    def or(f: PlanningDomainFunction) = createVarById(table.or(id, f.id), createName(f.name, '∨'))
    def and(f: PlanningDomainFunction) = createVarById(table.and(id, f.id), createName(f.name, '∧'))
    def xor(f: PlanningDomainFunction) = createVarById(table.xor(id, f.id), createName(f.name, '⊕'))
    def unary_! = createVarById(table.not(id), "¬(" + name + ')')
  }

  /** The trivial constant domain function () -> false. */
  object FalseVariable extends PlanningDomainFunction(0, "false")

  /** The trivial constant domain function () -> true. */
  object TrueVariable extends PlanningDomainFunction(1, "true")

  /** Represents an effect of an action. */
  case class Effect(condition: PlanningDomainFunction = TrueVariable, add: List[PlanningDomainFunction] = List(), del: List[PlanningDomainFunction] = List(), nondeterministic: Boolean = false)

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
  case class DomainAction(name: String, precondition: PlanningDomainFunction, effects: Effect*)
}