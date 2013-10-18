package alesia.planning.execution.actors

import scala.actors.Actor
import alesia.planning.execution.PlanExecutor
import alesia.planning.execution.ExecutionState
import alesia.planning.planners.Plan
import alesia.planning.planners.SingleActionPlan
import alesia.planning.context.ExecutionContext
import alesia.planning.PlanningProblem
import alesia.results.PlanExecutionResult
import alesia.results.FailurePlanExecutionResult

/**
 * Actor to execute a plan action-by-action.
 *  @author Roland Ewald
 */
case class PlanExecutionMaster(val slaves: Seq[PlanExecutionSlave]) extends ExecutionActor with PlanExecutor {

  require(slaves.nonEmpty, "List of slaves must not be empty.")
  slaves.foreach(_.start)
  start

  override def apply(data: ExecutionState): PlanExecutionResult = {
    val result = (this !! data).apply()
    result.asInstanceOf[PlanExecutionResult]
  }

  override def act = Actor.loop {
    react {
      case data: ExecutionState => reply { distributePlanOverSlaves(data) }
      case msg => reportUnsupported(msg)
    }
  }

  /** Executes a plan by letting the actions be executed by execution slaves. */
  private[this] def distributePlanOverSlaves(data: ExecutionState): PlanExecutionResult = data match {
    case _ => { (grabSlave() !! ???).apply(); FailurePlanExecutionResult(Seq(), new UnsupportedOperationException()) } //FIXME
  }

  /** Selects a slave for execution of an experiment action. */
  private[this] def grabSlave() = slaves.head
}