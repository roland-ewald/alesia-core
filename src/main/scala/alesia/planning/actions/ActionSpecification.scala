package alesia.planning.actions

import alesia.planning.context.ExecutionContext
import alesia.query.UserSpecification

/**
 * An action in the planning domain can be executed multiple times, while an action
 * implementing the {@link Action} trait should be executed just once (all specifics are given in the constructor).
 *
 * This difference is resolved by the action specification, which contains all necessary meta-data required by users and
 * the planning preparation mechanism.
 *
 * @see Action
 * @see PlanningPreparator
 *
 * @author Roland Ewald
 */
trait ActionSpecification[C, A <: Action[C]] {

  /** Precondition to create action. */
  def preCondition: ActionFormula

  /** Effect of the formula. */
  def effect: ActionFormula

  /** Factory method. */
  def createAction(logicalName: String, c: ExecutionContext): A

  /** Short name of the action. */
  def shortName: String

  /** Description of the action. */
  def description: String

  /**
   * Declares additional concrete actions to be specified, given the user specification and the actions already declared by different action specs.
   * Since some action specifications may need to 'react' on the declaration of other actions by declaring additional actions, this method is called
   * repeatedly and only *newly* declared actions should be returned.
   *
   * @param spec user specification
   * @param declaredActions holds action declarations that
   * @return *newly* declared actions
   */
  def declareConcreteActions(spec: UserSpecification, declaredActions: AllDeclaredActions): Seq[ActionDeclaration]

}

trait ActionDeclaration {
  def name: String
  def variables: Seq[String]
  def preCondition: ActionFormula
  def effect: ActionFormula
}