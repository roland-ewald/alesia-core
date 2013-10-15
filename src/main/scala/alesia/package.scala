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

  /**
   * Major execution sequence.
   *
   *  @param prep the planning preparator
   *  @param planner the planner
   *  @param executor the plan executor
   *  @param spec the problem specification
   *  @param selector the action selector
   *  @return the result of the plan execution
   */
  def run(prep: PlanningPreparator, planner: Planner, executor: PlanExecutor,
    spec: ProblemSpecification, selector: ActionSelector = FirstActionSelector): PlanExecutionResult = {

    val (problem, context) = prep.preparePlanning(spec)

    val plan = planner.plan(problem)

    plan match {
      case e: EmptyPlan => throw new IllegalStateException(
        s"Plan must not be empty. ${planner.getClass.getName} could not find a solution, please check your problem definition.")
      case _ => executor(ExecutionState(problem, plan, context))
    }

  }

}