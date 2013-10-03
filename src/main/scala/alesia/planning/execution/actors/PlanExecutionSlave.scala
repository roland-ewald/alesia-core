package alesia.planning.execution.actors

import scala.actors.Actor

import alesia.planning.context.ExecutionStatistics
import alesia.planning.context.LocalJamesExecutionContext

/**
 * Slave to execute single experiment actions.
 *  @author Roland Ewald
 */
case class PlanExecutionSlave() extends ExecutionActor {

  override def act = Actor.loop {
    react {
      case ActionJobMessage(action) => reply { action.execute(LocalJamesExecutionContext()); ActionJobDoneMessage(action) } //FIXME: execution context
      case msg => reportUnsupported(msg)
    }
  }

}

object PlanExecutionSlave {

  /** Create a number of execution slaves. */
  def apply(number: Int): Seq[PlanExecutionSlave] =
    (1 to number).map(_ => PlanExecutionSlave()).toList
}