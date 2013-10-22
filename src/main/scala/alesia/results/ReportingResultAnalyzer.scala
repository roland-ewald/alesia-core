package alesia.results

import alesia.planning.execution.ExecutionState
import alesia.planning.execution.ExecutionStepResult
import alesia.planning.execution.PlanState
import alesia.planning.context.ExecutionContext

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
    for (step <- steps.sliding(2).toSeq.zipWithIndex) yield reportAction(step._1(0), step._1(1), step._2 + 1)

  def reportAction(before: ExecutionStepResult, after: ExecutionStepResult, step: Int): ActionExecutionReport = {
    val actionIndex = after.action
    val action = before.newState.problem.declaredActions(actionIndex)
    ActionExecutionReport(step, actionIndex, action.name, before.newState.context.planState, after.newState.context.planState)
  }

  def reportState(step: ExecutionStepResult): StateReport = StateReport(step.newState)

}

case class PlanExecutionReport(init: Option[StateReport], actions: Seq[ActionExecutionReport], failure: Boolean, failureCause: Option[Throwable] = None)
case class StateReport(state: ExecutionState)
case class ActionExecutionReport(step: Int, index: Int, name: String, before: PlanState, after: PlanState)
