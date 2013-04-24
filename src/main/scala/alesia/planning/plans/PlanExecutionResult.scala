package alesia.planning.plans

import alesia.planning.actions.ActionResults

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