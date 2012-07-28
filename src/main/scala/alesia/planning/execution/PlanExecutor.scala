package alesia.planning.execution

import alesia.planning.plans.Plan

/** The plan executor. 
 * @author Roland Ewald
 */
trait PlanExecutor {
  
  /** Execute plan. */
  def execute(plan: Plan)

}