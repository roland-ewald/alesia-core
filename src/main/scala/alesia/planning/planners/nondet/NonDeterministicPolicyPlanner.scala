package alesia.planning.planners.nondet

import scala.annotation.tailrec
import scala.collection.Iterable

import alesia.planning.PlanningProblem
import alesia.planning.PlanningProblem
import alesia.planning.actions.ExperimentAction
import alesia.planning.context.Context
import alesia.planning.planners.Planner
import alesia.planning.plans.EmptyPlan
import alesia.planning.plans.EmptyPlan
import alesia.planning.plans.Plan
import alesia.utils.bdd.UniqueTable
import sessl.util.Logging

/**
 * Creates a plan assuming a non-deterministic environment, via techniques for symbolic model checking.
 * Implementation relies on ordered binary decision diagrams (OBDDs).
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

  override def plan(problem: PlanningProblem) = createPlan(problem)

  /**
   * Creates a plan.
   * @param problem the planning problem
   * @param planType the type of plan to be generated
   */
  def createPlan(problem: PlanningProblem, planType: NonDeterministicPlanTypes.Value = NonDeterministicPlanTypes.Strong) = {

    def logOutput(policy: Policy) = "New policy of iteration:\n" + policy.symbolicRepresentation
    implicit val domain = problem.table

    val initialState = problem.initialStateId //S_0
    val goalState = problem.goalStateId //S_g

    var previousPolicy: Policy = FailurePolicy //π
    var currentPolicy: Policy = EmptyPolicy //π'    
    var reachedStates = domain.union(goalState, currentPolicy.states) // S_π'∪ S_g

    // while π != π' and S_0 ⊈ S_π'∪ S_g  
    while (previousPolicy != currentPolicy && !domain.isContained(initialState, reachedStates)) {

      // Create pre-image, i.e. all (state,action) pairs whose results are included in (strong plans) 
      // or overlap with (weak plans) S_π'∪ S_g
      val preImage = findPreImage(reachedStates, problem, preImageCompareForPlanType(planType))

      // Create new policy π" by adding pre-image for those states that have not been reached yet
      val newPolicy = pruneStates(preImage, reachedStates, problem)

      // π <- π'
      previousPolicy = currentPolicy

      // π' <- π ∪ π"
      currentPolicy = currentPolicy ++ newPolicy

      // update S_π'∪ S_g
      reachedStates = domain.union(goalState, currentPolicy.states)

      logger.debug(logOutput(newPolicy))
    }

    //Check results and return plan
    if (domain.isContained(initialState, reachedStates))
      makeDeterministic(currentPolicy)
    else
      FailurePolicy
  }

  /** @return suitable comparison method for pre-image method to yield strong or weak plans */
  def preImageCompareForPlanType(t: NonDeterministicPlanTypes.Value) = t match {
    case NonDeterministicPlanTypes.Strong =>
      (effect: Int, states: Int, t: UniqueTable) => t.isContained(effect, states)
    case NonDeterministicPlanTypes.Weak =>
      (effect: Int, states: Int, t: UniqueTable) => !t.isEmpty(t.intersection(effect, states))
    case NonDeterministicPlanTypes.StrongCyclic =>
      throw new UnsupportedOperationException
  }

  /**
   *
   */
  def findPreImage(reachedStates: Int, problem: PlanningProblem,
    compare: (Int, Int, UniqueTable) => Boolean)(implicit tab: UniqueTable): Array[(Int, Int)] =
    problem.actions.zipWithIndex.map {
      case (action, index) =>
        {
          //TODO: make more efficient (by doing the work once, in the effects)
          def effectConj(e: problem.Effect) = e.add.map(_.id) ::: e.del.map(f => tab.not(f.id))
          def and(f: Int, g: Int) = tab.and(f, g)
          def or(f: Int, g: Int) = tab.or(f, g)

          //all deterministic effects are joined together via and
          val effect = action.effects.filter(!_.nondeterministic).flatMap(effectConj).foldLeft(1)(and)

          //all nondeterministic effects are joined together via or
          val effectWithNonDeterminism = action.effects.filter(_.nondeterministic).
            map(effectConj(_).foldLeft(1)(and)).map(and(effect, _)).foldLeft(effect)(or)

          //the precondition is joined via and to the effect
          val overallEffect = tab.and(action.precondition.id, effectWithNonDeterminism)

          logger.debug("Comparing expression for action #" + index + "\n with effects " +
            tab.structureOf(overallEffect, problem.variableNames).mkString("\n") +
            "\nwith current reachable state\n" + tab.structureOf(reachedStates, problem.variableNames).mkString("\n") +
            "accepted ? " + compare(overallEffect, reachedStates, tab))
          if (compare(overallEffect, reachedStates, tab)) {
            Some((problem.actions(index).precondition.id, index))
          } else None
        }
    }.flatten

  /**
   * Remove those actions that do not extend the set of reached states, and restrict those that do to the *new* states.
   * @param actions tuples of the form (precondition-function-id,action-index)
   * @param reachedStates the id of the characteristic function of the set of states that is currently reached
   * @param problem the planning problem
   * @return a policy containing
   */
  def pruneStates(actions: Iterable[(Int, Int)], reachedStates: Int, problem: PlanningProblem)(implicit tab: UniqueTable): Policy = {
    val prunedActions = actions.map {
      case (precond, actionIdx) => {
        val newStates = tab.difference(precond, reachedStates)
        if (!tab.isEmpty(newStates))
          Some(newStates, actionIdx)
        else None
      }
    }.flatten.toMap
    new NonDeterministicPolicy(problem, prunedActions, reachedStates)
  }

  /**
   * Creates a deterministic policy to be handed over as a result.
   * @param policy the policy that has been found
   * @return a corresponding plan
   */
  def makeDeterministic(policy: Policy): Plan = policy match {
    case EmptyPolicy => EmptyPolicy
    case FailurePolicy => FailurePolicy
    case pol: NonDeterministicPolicy => DeterministicPolicyPlan(pol)
  }
}