package alesia.planning.execution.actors

import scala.actors.Actor
import sessl.util.Logging
import alesia.bindings.ExperimentProvider

/** Slave to execute single experiment actions.
 *  @author Roland Ewald
 */
case class PlanExecutionSlave(implicit val provider: ExperimentProvider) extends ExecutionActor {

  override def act = Actor.loop {
    react {
      case ActionJobMessage(action) => reply { action.execute; ActionJobDoneMessage(action) }
      case msg => reportUnsupported(msg)
    }
  }

}

object PlanExecutionSlave {

  /** Create a number of execution slaves. */
  def apply(number: Int)(implicit provider: ExperimentProvider): Seq[PlanExecutionSlave] =
    (1 to number).map(_ => PlanExecutionSlave()).toList
}