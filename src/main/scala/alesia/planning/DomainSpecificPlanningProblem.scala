package alesia.planning

import alesia.planning.actions.ActionDeclaration

/**
 * Represents a [[PlanningProblem]] that is tied to a specific application domain.
 *
 * Hence, it provides additional data structures that link the domain entities with the entities
 * in the planning domain.
 *
 * @author Roland Ewald
 */
abstract class DomainSpecificPlanningProblem extends PlanningProblem {
  
  /** Specifies which declared action corresponds to which action index. */
  val declaredActions: Map[Int, ActionDeclaration]
  
  /** Specifies which formal action (in the planning domain) corresponds to which action index. */
  val planningActions: Map[Int, DomainAction]

}