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
trait ActionSpecification {

  /** Precondition to create action. */
  def preCondition: ActionFormula

  /** Effect of the formula. */
  def effect: ActionFormula

  /** Factory method. */
  def createAction(logicalName: String, c: ExecutionContext): Action[_]

  /** Short name of the action. */
  def shortName: String

  /** Default part of action names is short name without whitespace. */
  def shortActionName = shortName.replaceAll("\\s", "")

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

/** Declares an action. */
trait ActionDeclaration {
  def name: String
  def variables: Seq[String] = (preCondition.literals ++ effect.literals).map(_.name)
  def preCondition: ActionFormula
  def effect: ActionFormula
}

/** Straight-forward action declaration. */
case class SimpleActionDeclaration(name: String, 
  preCondition: ActionFormula = TrueFormula, effect: ActionFormula = TrueFormula) extends ActionDeclaration
