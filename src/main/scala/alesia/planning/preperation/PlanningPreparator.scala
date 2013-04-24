package alesia.planning.preperation

import alesia.query.UserSpecification
import alesia.planning.PlanningProblem
import alesia.planning.context.ExecutionContext

/**
 * Creates a suitable planning problem (and a context with which to start) from a user-defined hypothesis.
 * While the context is created to contain all implementation-specific data, the logical structure of the
 * planning problem is defined by the available actions.
 *
 * @see alesia.query
 *
 * @author Roland Ewald
 */
trait PlanningPreparator {

  def preparePlanning(h: UserSpecification): (PlanningProblem, ExecutionContext)

}