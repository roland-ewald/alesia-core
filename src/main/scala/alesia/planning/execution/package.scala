package alesia.planning

import alesia.planning.plans.Plan
import alesia.planning.context.ExecutionContext

/**
 * Some utilities for plan execution.
 *
 * @author: Roland Ewald
 */
package object execution {

  /**
   * Triplet containing the data required for execution. Contains the planning problem,
   * the plan to be executed, and the current execution context (may contain references to intermediate results etc.).
   */
  type ExecutionData = (PlanningProblem, Plan, ExecutionContext)

}