package alesia.planning.execution.actors

import scala.actors.Actor
import alesia.planning.execution.PlanExecutor
import sessl.util.Logging
import alesia.planning.plans.Plan

/** Actor to execute a plan action-by-action.
 *  @author Roland Ewald
 */
class PlanExecutionMaster extends ExecutionActor with PlanExecutor {

  override def execute(plan: Plan) {
    
  }

  override def act = Actor.loop {
    react {
      case PlanJobMessage(plan) => reply { distributePlanOverSlaves(plan); PlanJobDoneMessage(plan) }
      case msg => reportUnsupported(msg)
    }
  }

  private def distributePlanOverSlaves(plan: Plan) = {

  }
}