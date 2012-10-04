package alesia.planning.planners.nondet

import alesia.planning.PlanningProblem
import alesia.planning.context.Context
import alesia.planning.planners.Planner
import alesia.planning.plans.EmptyPlan
import alesia.planning.plans.EmptyPlan
import alesia.planning.plans.Plan
import sessl.util.Logging
import alesia.utils.bdd.UniqueTable
import scala.collection.SeqLike

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

    val domain = problem.table
    val initialState = problem.initialStateId //S_0
    val goalState = problem.goalStateId //S_g

    var previousPolicy: Policy = FailurePolicy //π
    var currentPolicy: Policy = EmptyPolicy //π'    
    var reached = domain.union(goalState, currentPolicy.state) // S_π'∪ S_g

    // while π != π' and S_0 ⊈ S_π'∪ S_g  
    while (previousPolicy != currentPolicy && !domain.isContained(initialState, reached)) {

      val preImage = strongPreImage(reached, problem)

      // Create π"
      val newPolicy = pruneStates(preImage, reached)

      // π <- π'
      previousPolicy = currentPolicy

      // π' <- π ∪ π"
      currentPolicy = currentPolicy ++ newPolicy

      // update S_π'∪ S_g
      reached = domain.union(goalState, currentPolicy.state)
    }

    if (domain.isContained(initialState, reached))
      makeDeterministic(currentPolicy)
    else
      FailurePolicy
  }

  //TODO:
  def strongPreImage(reachedStates: Int, problem: PlanningProblem): Array[(Int, Int)] =
    problem.actions.zipWithIndex.map(
      tuple =>
        {
          val (action, index) = tuple
          val effectSetFuncParts = action.effects.filter(!_.nondeterministic).flatMap {
            e => e.add.map(_.id) ::: e.del.map(f => problem.table.not(f.id))
          } //TODO: check correctness, make more efficient (by doing the work once, in the effects), deal with non-determinism
          val effect = effectSetFuncParts.foldLeft(1)((f, g) => problem.table.and(f, g))
          logger.info("Evaluating action '" + action.name + "'...")
          if (problem.table.isContained(effect, reachedStates)) {
            logger.info("Action '" + action.name + "' is applicable.")
            Some((index, effect))
          } else None
        }).flatten

  def pruneStates(actions: Seq[(Int, Int)], reachedStates: Int): Policy = {
	
    EmptyPolicy
  }

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