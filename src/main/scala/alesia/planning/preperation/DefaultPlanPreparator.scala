package alesia.planning.preperation

import alesia.query.UserSpecification
import alesia.planning.PlanningProblem
import alesia.planning.context.Context
import alesia.planning.context.EmptyContext

/**
 * Default plan preparation implementation.
 * 
 * @see PlanPreparator
 * 
 * @author Roland Ewald
 */
class DefaultPlanPreparator extends PlanPreparator {

  override def preparePlanning(specs: UserSpecification): (PlanningProblem, Context) = (null, EmptyContext)

}