package alesia.planning.planners.nondet

import scala.Array.canBuildFrom
import scala.Option.option2Iterable
import scala.collection.Iterable

import NonDeterministicPlanTypes.Strong
import NonDeterministicPlanTypes.StrongCyclic
import NonDeterministicPlanTypes.Weak
import alesia.planning.PlanningProblem
import alesia.planning.planners.Planner
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
   * Creates a plan of a certain type.
   */
  def createPlan(problem: PlanningProblem, planType: NonDeterministicPlanTypes.Value = Weak): Plan = {
    implicit val domain = problem.table
    planType match {
      case Strong => planWeakOrStrong(problem, strongPreImage)
      case StrongCyclic => planStrongCyclic(problem)
      case Weak => planWeakOrStrong(problem, weakPreImage)
      case _ => throw new UnsupportedOperationException
    }
  }

  /**
   * Creates a weak or a strong plan, depending on the preImage function.
   * @param p the planning problem
   * @param preImage the pre-image function to be used
   */
  def planWeakOrStrong(p: PlanningProblem, preImage: (Int, PlanningProblem) => Array[(Int, Int)])(implicit t: UniqueTable) = {

    import t._

    var previousPolicy: Policy = FailurePolicy //π
    var currentPolicy: Policy = EmptyPolicy //π'    
    var reachedStates = union(p.goalStates, currentPolicy.states) // S_g ∪ S_π' 

    // while π != π' and S_0 ⊈ S_π'∪ S_g  
    while (previousPolicy != currentPolicy && !isContained(p.initialStates, reachedStates)) {

      // Create pre-image, i.e. all (state,action) pairs whose results are included in (strong plans) 
      // or overlap with (weak plans) S_π'∪ S_g
      val preImg = preImage(reachedStates, p)

      // Create new policy π" by adding pre-image for those states that have not been reached yet
      val newPolicy = pruneStates(preImg, reachedStates, p)

      // π <- π'
      previousPolicy = currentPolicy

      // π' <- π ∪ π"
      currentPolicy = currentPolicy ++ newPolicy

      // update S_π'∪ S_g
      reachedStates = union(currentPolicy.states, p.goalStates)

      //Log progress
      this.logger.debug(logOutput(newPolicy))
    }

    createPlanIfPossible(p.initialStates, reachedStates, currentPolicy)
  }

  /**
   * Creates a strong-cyclic plan.
   * @param problem the planning problem
   */
  def planStrongCyclic(p: PlanningProblem)(implicit t: UniqueTable): Plan = {

    import t._

    var previousPolicy: Policy = EmptyPolicy //π
    var currentPolicy: Policy = Policy.universal(p) //π'
    while (previousPolicy != currentPolicy) {
      previousPolicy = currentPolicy
      currentPolicy = pruneUnconnected(pruneOutgoing(currentPolicy, p.goalStates), p.goalStates);
    }

    createPlanIfPossible(p.initialStates, union(p.goalStates, currentPolicy.states),
      removeNonProgress(currentPolicy, p.goalStates))
  }

  def pruneUnconnected(policy: Policy, goalStates: Int)(implicit t: UniqueTable) = {
    import t._
    //TODO
    policy
  }

  def pruneOutgoing(policy: Policy, goalStates: Int)(implicit t: UniqueTable) = {
    import t._
    //TODO 
    policy
  }

  def removeNonProgress(policy: Policy, goalStates: Int)(implicit t: UniqueTable) = {
    import t._
    //TODO
    policy
  }

  /** The pre-image function for weak plans.*/
  def weakPreImage(s: Int, p: PlanningProblem)(implicit t: UniqueTable) = findPreImage(s, p,
    (effect: Int, states: Int) => !t.isEmpty(t.intersection(effect, states)))

  /** The pre-image function for strong plans.*/
  def strongPreImage(s: Int, p: PlanningProblem)(implicit t: UniqueTable) =
    p.actions.map(_.strongPreImage(s)).zipWithIndex.filter(x => !t.isEmpty(x._1))

  /**
   * Pre-image function for both weak and strong plans.
   */
  def findPreImage(reachedStates: Int, problem: PlanningProblem,
    compare: (Int, Int) => Boolean)(implicit t: UniqueTable): Array[(Int, Int)] = {
    import t._
    problem.actions.zipWithIndex.map {
      case (action, index) =>
        {
          this.logger.debug("Comparing expression for action #" + index + "\n with effects " +
            t.structureOf(action.effect, problem.variableNames).mkString("\n") +
            "\nwith current reachable state\n" + structureOf(reachedStates, problem.variableNames).mkString("\n") +
            "accepted ? " + compare(action.effect, reachedStates))
          if (compare(action.effect, reachedStates)) {
            Some((problem.actions(index).precondition.id, index))
          } else None
        }
    }.flatten
  }

  /**
   * Remove those actions that do not extend the set of reached states, and restrict those that do to the *new* states.
   * @param actions tuples of the form (precondition-function-id,action-index)
   * @param reachedStates the id of the characteristic function of the set of states that is currently reached
   * @param problem the planning problem
   * @return a policy containing
   */
  def pruneStates(actions: Iterable[(Int, Int)], reachedStates: Int, problem: PlanningProblem)(implicit t: UniqueTable): Policy = {
    import t._
    val prunedActions = actions.map {
      case (precond, actionIdx) => {
        val newStates = difference(precond, reachedStates)
        if (!isEmpty(newStates))
          Some(newStates, actionIdx)
        else None
      }
    }.flatten.toMap
    new NonDeterministicPolicy(problem, prunedActions, reachedStates)
  }

  /**
   * Checks whether results are valid (the goal states can be reached from all initial states) and converts policy to deterministic plan.
   * @param initialStates the set of initial states
   * @param reachedStates the set of reached states
   * @param policy the policy that has been constructed
   * @return deterministic policy
   */
  def createPlanIfPossible(initialStates: Int, reachedStates: Int, policy: Policy)(implicit t: UniqueTable) = {
    if (t.isContained(initialStates, reachedStates))
      makeDeterministic(policy)
    else
      FailurePolicy
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

  /** Creates debugging output. */
  protected def logOutput(policy: Policy) = "New policy of iteration:\n" + policy.symbolicRepresentation
}