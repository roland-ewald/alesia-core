package alesia.planning.context

import alesia.bindings.ResourceProvider
import alesia.bindings.james.JamesExperimentProvider
import alesia.bindings.LocalResourceProvider
import alesia.query.UserDomainEntity
import alesia.query.UserPreference
import alesia.planning.execution.PlanState

/**
 * Execution context for the local execution of JAMES II experiments.
 * 
 * @see [[LocalResourceProvider]]
 * @see [[JamesExperimentProvider]]
 *
 * @author Roland Ewald
 */
class LocalJamesExecutionContext(val entities: Seq[UserDomainEntity], val preferences: Seq[UserPreference], val planState: PlanState) extends ExecutionContext {

  val resources = LocalResourceProvider

  val experiments = JamesExperimentProvider

}