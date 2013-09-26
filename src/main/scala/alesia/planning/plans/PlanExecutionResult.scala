package alesia.planning.plans

import alesia.planning.actions.ActionResults
import alesia.planning.execution.ExecutionState

/**
 * Super type of all plan results.
 *  @author Roland Ewald
 */
trait PlanExecutionResult {

  /** @return the execution trace for the given plan, as a sequence of action results */
  def trace: Seq[ActionResults]

}

case class FailurePlanExecutionResult(val states: Iterable[ExecutionState], cause: Throwable) extends PlanExecutionResult {
  override def trace = ???
}

case class FullPlanExecutionResult(val states: Iterable[ExecutionState]) extends PlanExecutionResult {
  override def trace = ???
}

