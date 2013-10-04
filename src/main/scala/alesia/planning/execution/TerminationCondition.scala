package alesia.planning.execution

import sessl.Duration
import sessl.AbstractDuration

/**
 * Class hierarchy to represent termination conditions as defined by the user.
 *
 * @author Roland Ewald
 */
trait TerminationCondition {

  /** @return true if execution should terminate */
  def apply(state: ExecutionState): Boolean

}

abstract class CompositeTerminationCondition(val conditions: Seq[TerminationCondition]) extends TerminationCondition {
  require(conditions.nonEmpty, "No termination conditions are given!")
}

/**
 * A disjunctions of termination conditions. The execution stops whenever a single one is true.
 * This is the default interpretation of encountering multiple termination conditions.
 *
 * @param conditions the termination conditions that form the conjunction
 */
case class TerminationDisjunction(override val conditions: TerminationCondition*)
  extends CompositeTerminationCondition(conditions) {
  override def apply(state: ExecutionState) = conditions.exists(_(state))
}

/**
 * A conjunctions of termination conditions. The execution stops only if _all_
 * given conditions are true.
 *
 * @param conditions the termination conditions that form the conjunction
 */
case class TerminationConjunction(override val conditions: TerminationCondition*)
  extends CompositeTerminationCondition(conditions) {
  override def apply(state: ExecutionState) = conditions.forall(_(state))
}

/**
 * Stop when a certain number of actions is reached.
 * @param max maximal number of actions to be executed
 */
case class MaxOverallNumberOfActions(val max: Int) extends TerminationCondition {

  require(max > 0, "Number of actions cannot be <= 0")

  override def apply(state: ExecutionState) = state.context.statistics.executedActions >= max
}

/**
 * Stop when a certain amount of wall-clock time has passed between starting and stopping experimentation.
 * @see [[Duration]]
 */
case class WallClockTimeMaximum(days: Int = 0, hours: Int = 0, minutes: Int = 0, seconds: Int = 0, milliseconds: Int = 0)
  extends AbstractDuration with TerminationCondition {

  require(toMilliSeconds > 0, "Overall duration must be >= 0")

  override def apply(state: ExecutionState) = {
    val duration = System.currentTimeMillis - state.context.statistics.startTime
    duration >= toMilliSeconds
  }
}

/**
 * Stop when a certain amount of CPU time has been consumed.
 * @see [[Duration]]
 */
case class CPUTimeMaximum(days: Int = 0, hours: Int = 0, minutes: Int = 0, seconds: Int = 0, milliseconds: Int = 0)
  extends AbstractDuration with TerminationCondition {

  require(toMilliSeconds > 0, "Overall duration must be >= 0")

  override def apply(state: ExecutionState) = ??? //TODO
}