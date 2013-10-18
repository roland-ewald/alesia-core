package alesia.results

/**
 * Analyzes a [[alesia.planning.plans.PlanExecutionResult]].
 *
 * @author Roland Ewald
 */
trait PlanExecutionResultAnalyzer[+A] {

  /**
   * Analyze plan execution result and return the result of this analysis.
   *  @param results the results of the plan execution, containing a sequence of [[alesia.planning.execution.ExecutionState]]
   */
  def apply(results: PlanExecutionResult): A

}