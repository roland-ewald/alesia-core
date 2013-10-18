package alesia.planning.execution

import alesia.planning.planners.Plan
import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.context.ExecutionContext

/**
 * All data required for execution of a single iteration.
 * Contains the [[alesia.planning.DomainSpecificPlanningProblem]], the [[alesia.planning.plans.Plan]] to be executed,
 * and the current [[alesia.planning.context.ExecutionContext]] (may contain references to intermediate results etc.).
 */
case class ExecutionState(problem: DomainSpecificPlanningProblem, plan: Plan, context: ExecutionContext) {

  /** Checks whether the execution can be finished because a goal state has been reached. */
  def isFinished: Boolean = {
    val currentState = problem.constructState(context.planState).id
    problem.table.isContained(currentState, problem.goalState.id)
  }

}