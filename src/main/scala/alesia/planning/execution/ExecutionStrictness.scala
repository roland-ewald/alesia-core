package alesia.planning.execution

import sessl.util.Logging

/**
 * Class hierarchy to deal with different levels of strictness during execution.
 *
 * For example, a strictness may choose to throw an exception when encountering certain kinds of errors.
 *
 * @see [[DefaultPlanExecutor]]
 *
 * @author Roland Ewald
 */
sealed trait ExecutionStrictness {
  def apply(msg: String): Unit
}

object FailEarly extends ExecutionStrictness {
  override def apply(msg: String) = throw new IllegalStateException(msg)
}

object IssueWarnings extends ExecutionStrictness with Logging {
  override def apply(msg: String) = logger.warn(msg)
}

object IngoreProblems extends ExecutionStrictness {
  override def apply(msg: String) = {}
}