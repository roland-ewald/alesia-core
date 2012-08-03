package alesia.planning.execution

import alesia.planning.plans.Plan
import alesia.planning.plans.PlanExecutionResult

/** The plan executor.
 *  @author Roland Ewald
 */
trait PlanExecutor {

  /** Execute plan. */
  def execute(plan: Plan): PlanExecutionResult

}