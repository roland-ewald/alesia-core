package alesia.planning

import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.Literal
import alesia.planning.execution.PlanState

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

  /** Maps a variable name to its corresponding function. */
  val functionByName: Map[String, PlanningDomainFunction]

  def constructState(xs: PlanState): PlanningDomainFunction = {
    if (xs.isEmpty)
      FalseVariable
    else
      xs.foldLeft(TrueVariable: PlanningDomainFunction)((state, x) => {
        val elemFunction = functionByName(x._1.name)
        state and (if (x._2) elemFunction else !elemFunction)
      })
  }

}