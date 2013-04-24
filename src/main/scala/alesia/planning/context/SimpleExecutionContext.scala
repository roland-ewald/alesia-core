package alesia.planning.context

import alesia.query.UserPreference

/**
 * Simple implementation of the system's execution context.
 *
 * @see ExecutionContext
 *
 * @author Roland Ewald
 */
class SimpleExecutionContext(val preferences: Seq[UserPreference]) extends ExecutionContext {

}