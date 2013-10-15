package alesia.planning.preparation

import alesia.query.ProblemSpecification
import alesia.planning.PlanningProblem
import alesia.planning.context.ExecutionContext
import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.execution.ActionSelector

/**
 * Creates a suitable [[alesia.planning.PlanningProblem]] (and an [[alesia.planning.context.ExecutionContext]] with
 * which to start it) from a user-defined [[alesia.query.ProblemSpecification]].
 *
 * While the [[alesia.planning.context.ExecutionContext]] is created to contain all implementation-specific data,
 * the logical structure of the planning problem is defined by the available actions.
 *
 * @see alesia.query
 *
 * @author Roland Ewald
 */
trait PlanningPreparator {

  /**
   * Prepare the planning step by creating [[alesia.planning.DomainSpecificPlanningProblem]] and
   * [[alesia.planning.context.ExecutionContext]] instances for the problem specification.
   * @param spec the problem specification
   * @return a domain-specific planning problem and its execution context
   */
  def preparePlanning(spec: ProblemSpecification): (DomainSpecificPlanningProblem, ExecutionContext)

}