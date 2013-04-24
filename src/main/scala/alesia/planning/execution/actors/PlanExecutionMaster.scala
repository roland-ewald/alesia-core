package alesia.planning.execution.actors

import scala.actors.Actor
import alesia.planning.execution.PlanExecutor
import alesia.planning.plans.EmptyPlanExecutionResult
import alesia.planning.plans.Plan
import alesia.planning.plans.PlanExecutionResult
import alesia.planning.plans.SingleActionPlan
import alesia.planning.context.ExecutionContext


/** Actor to execute a plan action-by-action.
 *  @author Roland Ewald
 */
case class PlanExecutionMaster(val slaves: Seq[PlanExecutionSlave]) extends ExecutionActor with PlanExecutor {

  require(slaves.nonEmpty, "List of slaves must not be empty.")
  slaves.foreach(_.start)
  start

  override def execute(plan: Plan, context:ExecutionContext): PlanExecutionResult = {
    val result = (this !! plan).apply()
    result.asInstanceOf[PlanExecutionResult]
  }

  override def act = Actor.loop {
    react {
      case plan: Plan => reply { distributePlanOverSlaves(plan) }
      case msg => reportUnsupported(msg)
    }
  }

  /** Executes a plan by letting the actions be executed by execution slaves. */
  private[this] def distributePlanOverSlaves(plan: Plan): PlanExecutionResult = plan match {
    case SingleActionPlan(a) => { (grabSlave() !! ActionJobMessage(a)).apply(); EmptyPlanExecutionResult }
  }

  /** Selects a slave for execution of an experiment action. */
  private[this] def grabSlave() = slaves.head
}