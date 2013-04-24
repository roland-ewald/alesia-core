package alesia.planning.preperation

import alesia.query.UserSpecification
import alesia.planning.PlanningProblem
import alesia.planning.context.ExecutionContext
import alesia.planning.context.EmptyExecutionContext

/**
 * Default plan preparation implementation.
 * 
 * @see PlanPreparator
 * 
 * @author Roland Ewald
 */
class DefaultPlanPreparator extends PlanPreparator {

  override def preparePlanning(specs: UserSpecification): (PlanningProblem, ExecutionContext) = (null, EmptyExecutionContext)

}