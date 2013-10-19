package alesia.results

import alesia.planning.execution.ExecutionState
import alesia.planning.execution.ExecutionStepResult

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

  def createReport(lastState: ExecutionStepResult, results: PlanExecutionResult): PlanExecutionReport = {

    val failureCause = failureCauseOf(results)
    val failure = !lastState._2.isFinished || failureCause.isDefined

    PlanExecutionReport(createActionReports(results.trace), failure, failureCause)
  }

  def createActionReports(steps: Seq[ExecutionStepResult]): Seq[ActionExecutionReport] = {
    for (step <- steps.tail) yield {
      ActionExecutionReport()
    }
  }

}

case class PlanExecutionReport(actions: Seq[ActionExecutionReport], failure: Boolean, failureCause: Option[Throwable] = None)

case class ActionExecutionReport()
