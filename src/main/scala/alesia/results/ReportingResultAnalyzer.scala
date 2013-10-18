package alesia.results

import alesia.planning.execution.ExecutionState
import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.planners.PlanExecutionResult
import alesia.planning.planners.FailurePlanExecutionResult

/**
 * Generates a user-readable report from the [[alesia.planning.plans.PlanExecutionResult]].
 *
 * @author Roland Ewald
 */
object ReportingResultAnalyzer extends PlanExecutionResultAnalyzer[PlanExecutionReport] {

  override def apply(results: PlanExecutionResult): PlanExecutionReport =
    results.trace.lastOption match {
      case None => PlanExecutionReport(Seq(), true, failureCauseOf(results))
      case Some(x) => createReport(x, results)
    }

  def failureCauseOf(result: PlanExecutionResult): Option[Throwable] =
    result match {
      case FailurePlanExecutionResult(_, cause) => Some(cause)
      case _ => None
    }

  def createReport(lastState: ExecutionState, results: PlanExecutionResult): PlanExecutionReport = {

    val failureCause = failureCauseOf(results)
    val failure = lastState.isFinished || failureCause.isDefined

    PlanExecutionReport(Seq(), failure, failureCause)
  }

}

case class PlanExecutionReport(actions: Seq[ActionExecutionReport], failure: Boolean, failureCause: Option[Throwable] = None)

case class ActionExecutionReport()