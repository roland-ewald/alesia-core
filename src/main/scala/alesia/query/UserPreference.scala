package alesia.query

import sessl.AbstractDuration
import alesia.planning.execution.ActionSelector
import alesia.planning.execution.TerminationCondition
import alesia.planning.execution.ExecutionStrictness

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

case class QSSMaxExecutionWallClockTime(val time: Double) extends UserPreference