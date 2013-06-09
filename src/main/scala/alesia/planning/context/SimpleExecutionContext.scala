package alesia.planning.context

import alesia.query.UserPreference
import alesia.query.UserDomainEntity

/**
 * Simple implementation of the system's execution context.
 *
 * @see ExecutionContext
 *
 * @author Roland Ewald
 */
class SimpleExecutionContext(val entities: Seq[UserDomainEntity], val preferences: Seq[UserPreference]) extends ExecutionContext