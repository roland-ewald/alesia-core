package alesia.planning

import alesia.planning.actions.ActionDeclaration
import alesia.planning.execution.PlanState

/**
 * Dummy used for testing.
 *
 * @see [[alesia.planning.execution.TestActionSelectors]]
 *
 * @author Roland Ewald
 */
object DummyDomainSpecificPlanningProblem extends DomainSpecificPlanningProblem {
  override val initialState = FalseVariable
  override val goalState = FalseVariable
  override val declaredActions = Map[Int, ActionDeclaration]()
  override val planningActions = Map[Int, DomainAction]()
  override def constructState(xs: PlanState): PlanningDomainFunction = ???
}