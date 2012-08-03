package alesia.planning.execution.actors

import alesia.planning.actions.ExperimentAction
import alesia.planning.plans.Plan

/** Super type of all messages exchanged for plan execution. */
sealed trait ExecutionMessage

/** Describes an experimentation action job to be done. */
case class ActionJobMessage(exp: ExperimentAction) extends ExecutionMessage

/** Describes an experimentation action job that has been done. */
case class ActionJobDoneMessage(exp: ExperimentAction) extends ExecutionMessage