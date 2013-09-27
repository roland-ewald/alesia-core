package alesia.planning.execution

import alesia.planning.plans.Plan
import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.context.ExecutionContext

/**
 * Triplet containing the data required for execution. Contains the domain-specific planning problem,
 * the plan to be executed, and the current execution context (may contain references to intermediate results etc.).
 */
case class ExecutionState(problem: DomainSpecificPlanningProblem, plan: Plan, context: ExecutionContext) {

  /** Checks whether the execution can be finished. */
  def isFinished: Boolean = problem.table.isContained(problem.constructState(context.planState).id, problem.goalState.id) 

}