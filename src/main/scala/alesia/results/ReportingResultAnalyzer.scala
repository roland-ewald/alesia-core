package alesia.results

import alesia.planning.plans.PlanExecutionResult

/**
 * Generates a user-readable report from the [[PlanExecutionResult]].
 *
 * @author Roland Ewald
 */
object ReportingResultAnalyzer extends PlanExecutionResultAnalyzer[PlanExecutionReport] {

  override def apply(results: PlanExecutionResult): PlanExecutionReport = {
    ??? //TODO: Finish this...
  }

}

case class PlanExecutionReport()