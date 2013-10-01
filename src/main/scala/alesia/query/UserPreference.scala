package alesia.query

import sessl.AbstractDuration
import alesia.planning.execution.ActionSelector
import alesia.planning.execution.TerminationCondition
import alesia.planning.execution.ExecutionStrictness

/** User preferences to consider during automatic experimentation. */
sealed trait UserPreference

/** Constrains the amount of real time that may pass between starting and stopping experimentation.*/
case class WallClockTimeMaximum(days: Int = 0, hours: Int = 0, minutes: Int = 0, seconds: Int = 0, milliseconds: Int = 0)
  extends AbstractDuration with UserPreference

/** Constrains the amount of CPU time that may be spent on experimentation. */
case class CPUTimeMaximum(days: Int = 0, hours: Int = 0, minutes: Int = 0, seconds: Int = 0, milliseconds: Int = 0)
  extends AbstractDuration with UserPreference

/**
 * Decides with which [[ActionSelector]] the execution should start.
 * Note that the selector may switch autonomously to some other selector between execution state transitions.
 */
case class StartWithActionSelector(selector: ActionSelector) extends UserPreference

/** Decides which [[TerminationCondition]] to use. */
case class TerminateWhen(val condition: TerminationCondition) extends UserPreference

/** Decides which [[ExecutionStrictness]] to apply. */
case class WithStrictness(val strictness: ExecutionStrictness) extends UserPreference