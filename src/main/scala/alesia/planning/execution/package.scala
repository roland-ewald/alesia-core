package alesia.planning

import alesia.query.UserDomainEntity

/**
 * Some utilities for plan execution.
 *
 * @author: Roland Ewald
 */
package object execution {

  type PlanState = Iterable[(String, Boolean)]

  type LinkChanges = Seq[(String, UserDomainEntity)]

  type LiteralLinks = Map[String, Seq[UserDomainEntity]]

}