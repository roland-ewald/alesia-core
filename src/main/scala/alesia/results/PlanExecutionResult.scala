package alesia.results

import alesia.planning.execution.ExecutionState
import alesia.planning.execution.ExecutionStepResult

/**
 * Super type of all plan results.
 *  @author Roland Ewald
 */
sealed trait PlanExecutionResult {

  /**
   * @return the execution trace for the given plan, as a sequence of [[alesia.planning.execution.ExecutionState]]
   *  instances
   */
  def trace: Seq[ExecutionStepResult]

  /** The first state is recorded in the trace as well, so decrement by one. */
  def numOfActions = trace.size - 1

}

case class FailurePlanExecutionResult(val states: Seq[ExecutionStepResult], cause: Throwable) extends PlanExecutionResult {
  override def trace = states
}

case class FullPlanExecutionResult(val states: Seq[ExecutionStepResult]) extends PlanExecutionResult {
  override def trace = states
}

