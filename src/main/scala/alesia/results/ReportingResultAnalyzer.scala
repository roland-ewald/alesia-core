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
      case None => PlanExecutionReport(None, Seq(), true, failureCauseOf(results))
      case Some(x) => createReport(x, results)
    }

  def failureCauseOf(result: PlanExecutionResult): Option[Throwable] =
    result match {
      case FailurePlanExecutionResult(_, cause) => Some(cause)
      case _ => None
    }

  def createReport(lastState: ExecutionStepResult, results: PlanExecutionResult): PlanExecutionReport = {
    val failureCause = failureCauseOf(results)
    val failure = !lastState.newState.isFinished || failureCause.isDefined
    PlanExecutionReport(Some(reportState(results.trace.head)), reportActions(results.trace), failure, failureCause)
  }

  def reportActions(steps: Seq[ExecutionStepResult]): Seq[ActionExecutionReport] =
    for (step <- steps.sliding(2).toSeq) yield reportAction(step(0), step(1))

  def reportAction(before: ExecutionStepResult, after: ExecutionStepResult): ActionExecutionReport = {
    ActionExecutionReport()
  }

  def reportState(state: ExecutionStepResult): InitialStateReport = {
    //    require(state.)
    ???
  }

}

case class PlanExecutionReport(init: Option[InitialStateReport], actions: Seq[ActionExecutionReport], failure: Boolean, failureCause: Option[Throwable] = None) {

}

case class InitialStateReport()

case class ActionExecutionReport()
