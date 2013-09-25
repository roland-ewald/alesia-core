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

/** The trivial result. */
case object EmptyPlanExecutionResult extends PlanExecutionResult {
  override def trace = Seq()
}

case class PlanExecutionFailureResult(val states: Iterable[ExecutionState], cause: Throwable) extends PlanExecutionResult {
  override def trace = ???
}

case class FullPlanResults(val states: Iterable[ExecutionState]) extends PlanExecutionResult {
  override def trace = ???
}

