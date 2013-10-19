package alesia.planning.execution

/**
 * Use this to store result for a single execution step. The executing the action with the given index resulted in
 * the given state.
 *
 * @author Roland Ewald
 */
case class ExecutionStepResult(action: Int, newState: ExecutionState)
