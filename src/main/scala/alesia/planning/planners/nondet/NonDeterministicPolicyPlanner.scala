package alesia.planning.planners.nondet

import alesia.planning.PlanningProblem
import alesia.planning.context.Context
import alesia.planning.planners.Planner
import alesia.planning.plans.EmptyPlan
import alesia.planning.plans.EmptyPlan
import alesia.planning.plans.Plan
import sessl.util.Logging
import alesia.utils.bdd.UniqueTable

/**
 * Creates a plan assuming a non-deterministic environment.
 *
 * The algorithms are mostly taken from chapter 17, p. 403 et sqq., of
 *
 * M. Ghallab, D. Nau, and P. Traverso, Automated Planning: Theory & Practice, 1st ed., ser. The Morgan Kaufmann Series in Artificial Intelligence.
 *
 * Available: http://www.worldcat.org/isbn/9781558608566
 *
 * @author Roland Ewald
 */
class NonDeterministicPolicyPlanner extends Planner with Logging {

  def plan(problem: PlanningProblem) = {
    //TODO: this finds strong plans, extend/generalize toward strong-cyclic and weak plans

    implicit val domain = problem.table
    var previousPolicy: Policy = FailurePolicy //π
    var currentPolicy: Policy = EmptyPolicy //π'
    val initialState = problem.initialStateId //S_0
    val goalState = problem.goalStateId //S_g

    var reached = union(goalState, currentPolicy.state) // S_π' ∪ S_g
    while (previousPolicy != currentPolicy && !domain.isContained(initialState, reached)) {
      val preImage = strongPreImage(reached)
      val newPolicy = pruneStates(preImage, reached) // π"
      previousPolicy = currentPolicy // π <- π'
      currentPolicy = currentPolicy ++ newPolicy // π' <- π ∪ π"
      reached = union(goalState, currentPolicy.state)
    }

    if (domain.isContained(initialState, reached))
      makeDeterministic(currentPolicy)
    else
      FailurePolicy
  }

  def union(set1: Int, set2: Int)(implicit table: UniqueTable): Int = table.or(set1, set2)

  //TODO:
  def strongPreImage(from: Int): Int = 0
  
  def pruneStates(preImg: Int, reachedStates: Int): Policy = EmptyPolicy

  def makeDeterministic(policy: Policy): Plan = new EmptyPlan {}
}

trait Policy extends Plan {
  def state: Int = 0
  def ++(other: Policy) = other
}

case class NonDetPolicy() extends Policy with EmptyPlan {
  override def decide(c: Context) = throw new UnsupportedOperationException //TODO
}

case object FailurePolicy extends Policy with EmptyPlan
case object EmptyPolicy extends Policy with EmptyPlan