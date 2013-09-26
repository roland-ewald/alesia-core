package alesia.planning.plans

import alesia.planning.execution.ExecutionState

/**
 * Super type of all plan results.
 *  @author Roland Ewald
 */
trait PlanExecutionResult {

  /** @return the execution trace for the given plan, as a sequence of [[ExecutionState]] instances */
  def trace: Seq[ExecutionState]

}

case class FailurePlanExecutionResult(val states: Seq[ExecutionState], cause: Throwable) extends PlanExecutionResult {
  override def trace = states
}

case class FullPlanExecutionResult(val states: Seq[ExecutionState]) extends PlanExecutionResult {
  override def trace = states
}

