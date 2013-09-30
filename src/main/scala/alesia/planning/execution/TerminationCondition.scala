package alesia.planning.execution

/**
 * @author Roland Ewald
 */
trait TerminationCondition {

  def apply(state: ExecutionState): Boolean

}

case class MaxOverallNumberOfActions(val max: Int) {
  def apply(state: ExecutionState) = ??? //TODO: create stats object in ExecutionContext
}