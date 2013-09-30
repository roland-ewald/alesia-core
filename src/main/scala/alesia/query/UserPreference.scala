package alesia.query

import sessl.AbstractDuration
import alesia.planning.execution.ActionSelector

/** User preferences to consider during automatic experimentation. */
trait UserPreference

/** Constrains the amount of real time that may pass between starting and stopping experimentation.*/
case class WallClockTimeMaximum(days: Int = 0, hours: Int = 0, minutes: Int = 0, seconds: Int = 0, milliseconds: Int = 0)
  extends AbstractDuration with UserPreference

/** Constrains the amount of CPU time that may be spent on experimentation. */
case class CPUTimeMaximum(days: Int = 0, hours: Int = 0, minutes: Int = 0, seconds: Int = 0, milliseconds: Int = 0)
  extends AbstractDuration with UserPreference

/**
 * Decides with which [[ActionSelector]] the execution should start.
 * The selector may switch autonomously to some other selector between an execution state transition.
 */
case class StartWithActionSelector(selector: ActionSelector) extends UserPreference