package alesia.planning.execution

import alesia.planning.plans.Plan
import alesia.planning.plans.PlanExecutionResult
import alesia.planning.context.ExecutionContext
import alesia.planning.PlanningProblem

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