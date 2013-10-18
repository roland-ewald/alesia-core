package alesia.planning.execution

import alesia.planning.planners.Plan
import alesia.planning.context.ExecutionContext
import alesia.planning.PlanningProblem
import alesia.results.PlanExecutionResult

/**
 * The plan executor.
 *
 *  @author Roland Ewald
 */
trait PlanExecutor {

  /**
   * Execute plan.
   *
   *  @param data the data required for execution
   *  @return the sequence of actions and their results
   */
  def apply(data: ExecutionState): PlanExecutionResult

}