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
   * Triplet containing the data required for execution. Contains the domain-specific planning problem,
   * the plan to be executed, and the current execution context (may contain references to intermediate results etc.).
   */
  case class ExecutionData(problem: DomainSpecificPlanningProblem, plan: Plan, context: ExecutionContext)

}