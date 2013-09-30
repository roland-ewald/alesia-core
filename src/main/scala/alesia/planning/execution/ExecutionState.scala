package alesia.planning.execution

import alesia.planning.plans.Plan
import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.context.ExecutionContext

/**
 * All data required for execution of a single iteration.
 * Contains the [[DomainSpecificPlanningProblem]], the [[Plan]] to be executed,
 * the current [[ExecutionContext]] (may contain references to intermediate results etc.),
 * and the current [[ActionSelector]].
 */
case class ExecutionState(problem: DomainSpecificPlanningProblem, plan: Plan, context: ExecutionContext) {

  /** Checks whether the execution can be finished because a goal state has been reached. */
  def isFinished: Boolean = {
    val currentState = problem.constructState(context.planState).id
    problem.table.isContained(currentState, problem.goalState.id)
  }

}