package alesia.planning.actions

import alesia.planning.context.ExecutionContext
import alesia.planning.execution.StateUpdate
import sessl.util.Logging

/**
 * General interface for actions. An action is completely pre-configured and ready to be executed.
 *
 *  @param [A] the context provider required to execute the action
 *  @author Roland Ewald
 */
trait Action extends Logging {

  /** Execute the action. */
  def execute(c: ExecutionContext): StateUpdate

}