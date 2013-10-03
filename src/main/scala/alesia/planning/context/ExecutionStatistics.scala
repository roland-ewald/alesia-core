package alesia.planning.context

/**
 * Holds some overall statistics about the current execution, as part of the [[ExecutionContext]].
 *
 * @author Roland Ewald
 */
case class ExecutionStatistics(val startTime: Long = System.currentTimeMillis, val executedActions: Int = 0) {

  /** Create up-to-date execution statistics after executing an action. */
  def actionExecuted = ExecutionStatistics(startTime, executedActions + 1)

}