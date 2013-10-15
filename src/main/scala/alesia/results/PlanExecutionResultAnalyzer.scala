package alesia.results

import alesia.planning.plans.PlanExecutionResult

/**
 * Analyzes a [[alesia.planning.plans.PlanExecutionResult]].
 *
 * @author Roland Ewald
 */
trait PlanExecutionResultAnalyzer[+A] {

  /** Analyze plan execution result and return the result of this analysis. */
  def apply(results: PlanExecutionResult): A

}