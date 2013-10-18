package alesia

import alesia.planning.preparation.PlanningPreparator
import alesia.planning.planners.Planner
import alesia.planning.execution.PlanExecutor
import alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
import alesia.planning.preparation.DefaultPlanningPreparator
import alesia.planning.execution.DefaultPlanExecutor
import alesia.planning.planners.PlanExecutionResult

/**
 * Specifies default implementations of [[alesia.planning.preparation.PlanningPreparator]], 
 * [[alesia.planning.planners.Planner]], and [[alesia.planning.execution.PlanExecutor]].
 *
 * It also provides a method `submit`, to submit the planning problem to the system.
 */
package object query {

  val defaultPreparator: PlanningPreparator = new DefaultPlanningPreparator

  val defaultPlanner: Planner = new NonDeterministicPolicyPlanner

  val defaultExecutor: PlanExecutor = DefaultPlanExecutor

  type ProblemSpecification = (Seq[UserDomainEntity], Seq[UserPreference], UserHypothesis)

  def submit(prep: PlanningPreparator, planner: Planner, executor: PlanExecutor)(dom: UserDomainEntity*)(prefs: UserPreference*)(hyp: UserHypothesis): PlanExecutionResult =
    alesia.run(prep, planner, executor, (dom, prefs, hyp))

  def submit(dom: UserDomainEntity*)(prefs: UserPreference*)(hyp: UserHypothesis): PlanExecutionResult =
    submit(defaultPreparator, defaultPlanner, defaultExecutor)(dom: _*)(prefs: _*)(hyp)

}