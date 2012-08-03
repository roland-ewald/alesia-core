package alesia.planning.plans

/** Super type of all plan results.
 *  @author Roland Ewald
 */
trait PlanExecutionResult

/** The trivial result. */
case class EmptyPlanExecutionResult() extends PlanExecutionResult