package alesia.planning.execution.actors

import scala.actors.Actor
import sessl.util.Logging

/** Slave to execute single experiment actions.
 *  @author Roland Ewald
 */
class PlanExecutionSlave(implicit val provider: alesia.bindings.ExperimentProvider) extends ExecutionActor {

  override def act = Actor.loop {
    react {
      case ActionJobMessage(action) => reply { action.execute; ActionJobDoneMessage(action) }
      case msg => reportUnsupported(msg)
    }
  }

}