package alesia.planning.context

import alesia.query.UserDomainEntity
import alesia.query.UserPreference
import alesia.planning.execution.PlanState
import alesia.bindings.james.JamesExperimentProvider
import alesia.bindings.LocalResourceProvider
import alesia.planning.execution.ActionSelector
import alesia.planning.execution.FirstActionSelector

/**
 * Execution context for the local execution of JAMES II experiments.
 *
 * @see [[alesia.bindings.LocalResourceProvider]]
 * @see [[alesia.bindings.james.JamesExperimentProvider]]
 *
 * @author Roland Ewald
 */
case class LocalJamesExecutionContext(
  val entities: Seq[UserDomainEntity] = Seq(),
  val preferences: Seq[UserPreference] = Seq(),
  val planState: PlanState = Seq(),
  val entitiesForLiterals: Map[String, Seq[UserDomainEntity]] = Map(),
  val actionSelector: ActionSelector = FirstActionSelector,
  val statistics: ExecutionStatistics = ExecutionStatistics())
  extends ExecutionContext {

  val resources = LocalResourceProvider

  val experiments = JamesExperimentProvider

}