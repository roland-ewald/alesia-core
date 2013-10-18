package alesia.planning

import alesia.planning.actions.ActionDeclaration
import alesia.planning.execution.PlanState

/**
 * Represents a [[alesia.planning.PlanningProblem]] that is tied to a specific application domain.
 *
 * Hence, it provides additional data structures that link the domain entities with the entities
 * in the planning domain.
 *
 * @author Roland Ewald
 */
trait DomainSpecificPlanningProblem extends PlanningProblem {

  /** Specifies which declared action corresponds to which action index. */
  val declaredActions = Map[Int, ActionDeclaration]()

  /** Specifies which formal action (in the planning domain) corresponds to which action index. */
  val planningActions = Map[Int, DomainAction]()

  /** Creates the representation of the [[alesia.planning.execution.PlanState]] in the planning domain. */
  def constructState(xs: PlanState): PlanningDomainFunction

}