package alesia.planning.preparation

import alesia.query.ProblemSpecification
import alesia.planning.PlanningProblem
import alesia.planning.context.ExecutionContext

/**
 * Creates a suitable [[PlanningProblem]] (and an [[ExecutionContext]] with which to start it) from a user-defined [[ProblemSpecification]].
 * While the [[ExecutionContext]] is created to contain all implementation-specific data, the logical structure of the
 * planning problem is defined by the available actions.
 *
 * @see alesia.query
 *
 * @author Roland Ewald
 */
trait PlanningPreparator {

  /**
   * Prepare the planning step by creating [[PlanningProblem]] and [[ExecutionContext]] instances for the problem specification
   * @param spec the problem specification
   */
  def preparePlanning(spec: ProblemSpecification): (PlanningProblem, ExecutionContext)

}