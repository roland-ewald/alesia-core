package alesia.planning

import alesia.query.UserDomainEntity

/**
 * This package contains the component that execute a plan.
 */
package object execution {

  /**
   * The current state in the [[alesia.planning.PlanningDomain]], simply stored as tuples of the form
   * {{{(literal_name,[true||false])}}}. Stored in [[alesia.planning.context.ExecutionContext]].
   */
  type PlanState = Iterable[(String, Boolean)]

  /**
   * Tuples {{{(literal_name,domain_entity)}}} that shall be changed. Managed by
   * [[alesia.planning.execution.StateUpdate]].
   */
  type LinkChanges = Seq[(String, UserDomainEntity)]

  /**
   * Represents links between literals and (arbitrarily many) [[alesia.query.UserDomainEntity]] instances.
   */
  type LiteralLinks = Map[String, Seq[UserDomainEntity]]
}