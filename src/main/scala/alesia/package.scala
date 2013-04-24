import alesia.planning.execution.PlanExecutor
import alesia.planning.planners.Planner
import alesia.planning.plans.PlanExecutionResult
import alesia.planning.preperation.PlanPreparator
import alesia.query.UserSpecification

/**
 * General type definitions and methods.
 *
 * @author Roland Ewald
 */
package object alesia {

  /** Major execution sequence. */
  def run(prep: PlanPreparator, planner: Planner, executor: PlanExecutor, spec: UserSpecification): PlanExecutionResult = {
    val (problem, context) = prep.preparePlanning(spec)
    val plan = planner.plan(problem)
    executor.execute(plan, context)
  }

}