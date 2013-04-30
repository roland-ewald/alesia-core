package alesia.planning.actions

import alesia.planning.context.ExecutionContext

/**
 * An action in the planning domain can be executed multiple times, while an action
 * implementing the {@link Action} trait should be executed just once (all specifics are given in the constructor).
 *
 * This difference is resolved by the action specification
 *
 * @see Action
 *
 * @author Roland Ewald
 */
trait ActionSpecification[C, A <: Action[C]] {

  /** Precondition to create action. */
  def preCondition: Option[ActionFormula]

  /** Effect of the formula. */
  def effect: ActionFormula

  /** Declared public literals. */
  def publicLiterals: Seq[Literal]

  /** Declared private literals. */
  def privateLiterals: Seq[Literal]

  /** Factory method. */
  def createAction(logicalName: String, c: ExecutionContext): A
  
  /** Short name of the action. */
  def shortName: String
  
  /** Description of the action. */
  def description: String
}