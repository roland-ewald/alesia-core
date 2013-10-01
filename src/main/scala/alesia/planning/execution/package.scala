package alesia.planning

import alesia.query.UserDomainEntity

/**
 * Some utilities for plan execution.
 *
 * @author: Roland Ewald
 */
package object execution {

  /**
   * The current state in the [[PlanningDomain]], simply stored as tuples of the form
   * {{{(literal_name,[true||false])}}}. Stored in [[ExecutionContext]].
   */
  type PlanState = Iterable[(String, Boolean)]

  /**
   * Tuples {{{(literal_name,domain_entity)}}} that shall be changed. Managed by [[StateUpdate]].
   */
  type LinkChanges = Seq[(String, UserDomainEntity)]

  /**
   * Represents links between literals and (arbitrarily many) [[UserDomainEntity]] instances.
   */
  type LiteralLinks = Map[String, Seq[UserDomainEntity]]

}