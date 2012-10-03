package alesia.planning.planners.nondet

import alesia.planning.PlanningProblem
import alesia.planning.context.Context
import alesia.planning.planners.Planner
import alesia.planning.plans.EmptyPlan
import alesia.planning.plans.EmptyPlan
import alesia.planning.plans.Plan
import sessl.util.Logging

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

    var previousPolicy: Policy = FailurePolicy //π
    var currentPolicy: Policy = EmptyPolicy //π'
    val initialState = problem.initialStateId //S_0
    val goalState = problem.goalStateId //S_g

    var reached = union(goalState, currentPolicy.state) // S_π' ∪ S_g
    while (previousPolicy != currentPolicy && !isContained(initialState, reached)) {
      val preImage = strongPreImage(reached)
      val newPolicy = pruneStates(preImage, reached) // π"
      previousPolicy = currentPolicy // π <- π'
      currentPolicy = currentPolicy ++ newPolicy // π' <- π ∪ π"
      reached = union(goalState, currentPolicy.state)
    }

    if (isContained(initialState, reached))
      makeDeterministic(currentPolicy)
    else
      FailurePolicy
  }

  //TODO:

  def makeDeterministic(policy: Policy): Plan = new EmptyPlan {}

  def pruneStates(preImg: Int, reachedStates: Int): Policy = EmptyPolicy

  def strongPreImage(from: Int): Int = 0

  def isContained(set1: Int, set2: Int): Boolean = true

  def union(set1: Int, set2: Int): Int = 0
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