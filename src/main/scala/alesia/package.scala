import alesia.planning.execution.PlanExecutor
import alesia.planning.planners.Planner
import alesia.planning.plans.PlanExecutionResult
import alesia.planning.preparation.PlanningPreparator
import alesia.query.ProblemSpecification
import alesia.planning.plans.EmptyPlan
import alesia.planning.execution.ExecutionState
import alesia.planning.execution.FirstActionSelector
import alesia.planning.execution.ActionSelector

/**
 * General type definitions and methods.
 *
 * @author Roland Ewald
 */
package object alesia {
 
  /** Major execution sequence. */
  def run(prep: PlanningPreparator, planner: Planner, executor: PlanExecutor,
    spec: ProblemSpecification, selector: ActionSelector = FirstActionSelector): PlanExecutionResult = {       
    val (problem, context) = prep.preparePlanning(spec)
    val plan = planner.plan(problem)
    require(!plan.isInstanceOf[EmptyPlan],
      s"Plan must not be empty. ${planner.getClass.getName} could not find a solution, please check your problem definition.")
    executor(ExecutionState(problem, plan, context))
  }

}