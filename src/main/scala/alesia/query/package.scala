package alesia

import planning.preparation.PlanningPreparator
import planning.planners.Planner
import planning.execution.PlanExecutor
import planning.planners.nondet.NonDeterministicPolicyPlanner
import planning.preparation.DefaultPlanningPreparator
import planning.execution.DefaultPlanExecutor
import results.PlanExecutionResult
import planning.execution.ExecutionState
import planning.execution.ActionSelector
import planning.planners.EmptyPlan
import planning.execution.FirstActionSelector

/**
 * Specifies default implementations of [[alesia.planning.preparation.PlanningPreparator]],
 * [[alesia.planning.planners.Planner]], and [[alesia.planning.execution.PlanExecutor]].
 *
 * It also provides a method `submit`, to submit the planning problem to the system.
 */
package object query {

  val defaultPlanner: Planner = new NonDeterministicPolicyPlanner

  val defaultExecutor: PlanExecutor = DefaultPlanExecutor

  type ProblemSpecification = (Seq[UserDomainEntity], Seq[UserPreference], UserHypothesis)

  def submit(prep: PlanningPreparator, planner: Planner, executor: PlanExecutor)(dom: UserDomainEntity*)(prefs: UserPreference*)(hyp: UserHypothesis): PlanExecutionResult =
    run(prep, planner, executor, (dom, prefs, hyp))

  def submit(dom: UserDomainEntity*)(prefs: UserPreference*)(hyp: UserHypothesis): PlanExecutionResult =
    submit(DefaultPlanningPreparator, defaultPlanner, defaultExecutor)(dom: _*)(prefs: _*)(hyp)

  def submit(s: Scenario): PlanExecutionResult = submit(s.domain: _*)(s.preferences: _*)(s.hypothesis)

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