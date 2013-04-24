package alesia

import alesia.planning.plans.PlanExecutionResult
import alesia.planning.preperation.PlanningPreparator
import alesia.planning.planners.Planner
import alesia.planning.execution.PlanExecutor
import alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
import alesia.planning.preperation.DefaultPlanningPreparator
import alesia.planning.execution.DefaultPlanExecutor

package object query {

  val defaultPreparator: PlanningPreparator = new DefaultPlanningPreparator

  val defaultPlanner: Planner = new NonDeterministicPolicyPlanner

  val defaultExecutor: PlanExecutor = new DefaultPlanExecutor

  type UserSpecification = (Seq[UserDomainEntity], Seq[UserPreference], UserHypothesis)

  def submit(prep: PlanningPreparator, planner: Planner, executor: PlanExecutor)(dom: UserDomainEntity*)(prefs: UserPreference*)(hyp: UserHypothesis): PlanExecutionResult =
    run(prep, planner, executor, (dom, prefs, hyp))

  def submit(dom: UserDomainEntity*)(prefs: UserPreference*)(hyp: UserHypothesis): PlanExecutionResult =
    submit(defaultPreparator, defaultPlanner, defaultExecutor)(dom: _*)(prefs: _*)(hyp)

}