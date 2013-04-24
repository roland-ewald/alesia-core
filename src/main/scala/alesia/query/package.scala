package alesia

import alesia.planning.plans.PlanExecutionResult
import alesia.planning.preperation.PlanPreparator
import alesia.planning.planners.Planner
import alesia.planning.execution.PlanExecutor
import alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
import alesia.planning.preperation.DefaultPlanPreparator
import alesia.planning.execution.DefaultPlanExecutor

package object query {

  val defaultPreparator: PlanPreparator = new DefaultPlanPreparator

  val defaultPlanner: Planner = new NonDeterministicPolicyPlanner

  val defaultExecutor: PlanExecutor = new DefaultPlanExecutor

  type UserSpecification = (Seq[UserDomainEntity], Seq[UserPreference], UserHypothesis)

  def submit(prep: PlanPreparator, planner: Planner, executor: PlanExecutor)(dom: UserDomainEntity*)(prefs: UserPreference*)(hyp: UserHypothesis): PlanExecutionResult =
    run(prep, planner, executor, (dom, prefs, hyp))

  def submit(dom: UserDomainEntity*)(prefs: UserPreference*)(hyp: UserHypothesis): PlanExecutionResult =
    submit(defaultPreparator, defaultPlanner, defaultExecutor)(dom: _*)(prefs: _*)(hyp)

}