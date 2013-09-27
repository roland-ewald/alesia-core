package alesia.planning

import alesia.planning.plans.Plan
import alesia.planning.context.ExecutionContext
import alesia.planning.actions.Literal
import alesia.query.UserDomainEntity

/**
 * Some utilities for plan execution.
 *
 * @author: Roland Ewald
 */
package object execution {

  type PlanState = Iterable[(String, Boolean)]
  
  type LinkChanges = Seq[(String, UserDomainEntity)]

}