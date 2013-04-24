package alesia.planning.preparation

import alesia.planning.PlanningProblem
import alesia.planning.context.ExecutionContext
import alesia.planning.context.SimpleExecutionContext
import alesia.query.UserSpecification

/**
 * Default plan preparation implementation.
 *
 * @see PlanPreparator
 *
 * @author Roland Ewald
 */
class DefaultPlanningPreparator extends PlanningPreparator {

  override def preparePlanning(spec: UserSpecification): (PlanningProblem, ExecutionContext) = {

    val domainEntities = spec._1
    val hypothesis = spec._3

    val problem = new PlanningProblem() {
      val initialState = FalseVariable
      val goalState = FalseVariable
    }

    (problem, new SimpleExecutionContext(spec._2))
  }

}