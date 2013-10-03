package alesia.planning.context

import alesia.bindings.ExperimentProvider
import alesia.bindings.ResourceProvider
import alesia.planning.execution.ActionSelector
import alesia.planning.execution.LiteralLinks
import alesia.planning.execution.PlanState
import alesia.query.UserDomainEntity
import alesia.query.UserPreference
import alesia.utils.misc.CollectionHelpers.filterType

/**
 * The current execution context of a plan. On this basis, a [[PlanExecutor]] triggers the [[Plan]] to decides upon
 * the next action(s).
 *
 * Contains references to intermediate results as [[UserDomainEntity]] instances, user preferences in the form of
 * [[UserPreference]] instances, and (more generally) all other data that does not need to be represented on the
 * level on which the planning sub-system operates.
 *
 * @author Roland Ewald
 */
trait ExecutionContext {

  /**
   * The user preferences regarding execution.
   * @return user preferences
   */
  val preferences: Seq[UserPreference]

  /**
   * The available domain entities.
   * @return domain entities
   */
  val entities: Seq[UserDomainEntity]

  /**
   * The [[ActionSelector]] that is currently used. May change between successive contexts.
   * @return the current action selector
   */
  val actionSelector: ActionSelector

  /**
   * The current state in the planning domain.
   * The [[PlanExecutor]] calls [[DomainSpecificPlanningProblem#constructState]] with this, which yields a boolean
   * function that can be interpreted by the [[Plan]].
   * @return current plan state
   */
  val planState: PlanState

  /**
   * Stores the links between literals (planning domain) and [[UserDomainEntity]] instances (execution domain).
   */
  val entitiesForLiterals: LiteralLinks

  /**
   * Aggregated execution statistics. Used by [[TerminationCondition]] instances.
   */
  val statistics: ExecutionStatistics

  /**
   * Retrieve preferences of a given type.
   * @return all preferences of the given type
   */
  def preferencesOf[T <: UserPreference](implicit m: Manifest[T]): Seq[T] = filterType[T](preferences)

  /**
   * Retrieve all [[UserDomainEntity]] instances of a given type.
   * @return all domain entities of the given type
   */
  def entitiesOf[T <: UserDomainEntity](implicit m: Manifest[T]): Seq[T] = filterType[T](entities)

  /** Provides all external resources (like files, URLs, artifacts).*/
  val resources: ResourceProvider

  /** Provides all executable experiments. This is where the simulation system is hidden. */
  val experiments: ExperimentProvider

}