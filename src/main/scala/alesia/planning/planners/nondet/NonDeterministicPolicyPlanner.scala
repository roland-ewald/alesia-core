package alesia.planning.planners.nondet

import scala.tools.nsc.transform.Flatten
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
import scala.annotation.tailrec

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

  //TODO: this finds strong plans, extend/generalize toward strong-cyclic and weak plans

  def plan(problem: PlanningProblem) = {

    def logOutput(policy: Policy) = "New policy of iteration:\n" + policy.symbolicRepresentation
    implicit val domain = problem.table
    
    val initialState = problem.initialStateId //S_0
    val goalState = problem.goalStateId //S_g

    var previousPolicy: Policy = FailurePolicy //π
    var currentPolicy: Policy = EmptyPolicy //π'    
    var reachedStates = domain.union(goalState, currentPolicy.states) // S_π'∪ S_g

    // while π != π' and S_0 ⊈ S_π'∪ S_g  
    while (previousPolicy != currentPolicy && !domain.isContained(initialState, reachedStates)) {

      // Create pre-image, i.e. all (state,action) pairs whose results are in S_π'∪ S_g
      val preImage = strongPreImage(reachedStates, problem)

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

  def strongPreImage(reachedStates: Int, problem: PlanningProblem)(implicit tab: UniqueTable): Array[(Int, Int)] =
    problem.actions.zipWithIndex.map {
      case (action, index) =>
        {
          logger.info("Evaluating action '" + action.name + "'...")
          val effect = action.effects.filter(!_.nondeterministic).flatMap {
            e => e.add.map(_.id) ::: e.del.map(f => tab.not(f.id))
          }.foldLeft(1)((f, g) => tab.and(f, g))

          val nonDeterministicActions = action.effects.filter(_.nondeterministic)

          //TODO: check correctness, make more efficient (by doing the work once, in the effects), deal with non-determinism          
          if (tab.isContained(effect, reachedStates)) {
            logger.info("Action '" + action.name + "' is applicable.")
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