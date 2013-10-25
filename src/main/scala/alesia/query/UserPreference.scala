package alesia.query

import sessl.AbstractDuration
import alesia.planning.execution.ActionSelector
import alesia.planning.execution.TerminationCondition
import alesia.planning.execution.ExecutionStrictness
import sessl.Duration

/** User preferences to consider during automatic experimentation. */
sealed trait UserPreference

/**
 * Decides with which [[alesia.planning.execution.ActionSelector]] the execution should start.
 * Note that the selector may switch autonomously to some other selector between execution state transitions.
 */
case class StartWithActionSelector(selector: ActionSelector) extends UserPreference

/** Decides which [[alesia.planning.execution.TerminationCondition]] to use. */
case class TerminateWhen(val condition: TerminationCondition) extends UserPreference

/** Decides which [[alesia.planning.execution.ExecutionStrictness]] to apply. */
case class WithStrictness(val strictness: ExecutionStrictness) extends UserPreference

/**
 * Determine how long *any* simulation execution should take at most.
 * Many actions will likely interpret this as a soft boundary, i.e., this is really just a
 *  *preference* and not enforced by the system.
 */
case class MaxSingleExecutionWallClockTime(days: Int = 0, hours: Int = 0, minutes: Int = 0, seconds: Int = 0, milliseconds: Int = 0) extends AbstractDuration with UserPreference

/** The permissible QSS error that is allowed. */
case class MaxAllowedQSSError(relativeError: Double = 0.2) extends UserPreference