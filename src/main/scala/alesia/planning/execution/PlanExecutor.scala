package alesia.planning.execution

import alesia.planning.plans.Plan
import alesia.planning.plans.PlanExecutionResult
import alesia.planning.context.ExecutionContext

/**
 * The plan executor.
 *  @author Roland Ewald
 */
trait PlanExecutor {

  /** Execute plan. 
   *  @param plan the plan to be executed
   *  @param context the current execution context (may contain references to intermediate results etc.)*/
  def execute(plan: Plan, context: ExecutionContext): PlanExecutionResult

}