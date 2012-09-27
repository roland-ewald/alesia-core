package alesia.planning.planners.nondet

import alesia.planning.planners.Planner
import alesia.planning.plans.EmptyPlan
import alesia.planning.PlanningProblem

/**
 * Creates a plan assuming a non-deterministic environment.
 *
 * @author Roland Ewald
 */
class NonDeterministicPolicyPlanner extends Planner {

  def plan(problem: PlanningProblem) = {
    EmptyPlan
  }

}