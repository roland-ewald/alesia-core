package alesia.planning.execution.actors

import scala.actors.Actor
import sessl.util.Logging

/** Common super class for all actors involved in the execution of a plan. Mostly holds auxiliary methods.
 *  @author Roland Ewald
 */
abstract class ExecutionActor extends Actor with Logging {

  /** Report that the given message is not supported. */
  protected def reportUnsupported(msg: Any) = logger.error("Message '" + msg + "' not supported.")
}