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
import alesia.planning.plans.EmptyPlan
import alesia.planning.PlanningDomain
import alesia.planning.PlanningDomainAction
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer

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
   * Creates a strong-cyclic plan. Implementation as developed by Rintanen, see his script fig. 4.6.
   * @param problem the planning problem
   * @return conditional plan
   */
  def planStrongCyclic(p: PlanningProblem)(implicit t: UniqueTable): Plan = {
    import t._


    val G = p.goalStates

    var W = -1
    var W_new = G
    while (!isContained(p.initialStates, pruneStrongCyclic(p.actions, W_new, G)) && W_new != W) {
      W = W_new
      // W_I = W_(i-1) ∪  (∪_(o \in O) (wpreimg_o(W_(i-1))):
      W_new = p.actions.map(_.weakPreImage(W)).foldLeft(W)(union)
    }

    if (!isContained(p.initialStates, W_new))
      return FailurePolicy

    val D_i = ArrayBuffer[Int]()
    D_i += G
    val L = pruneStrongCyclic(p.actions, W_new, G)

    var S = -1
    var S_new = G
    while (S_new != S) {
      S = S_new
      S_new = p.actions.map(a => intersection(a.weakPreImage(S_new), a.strongPreImage(union(L, S_new)))).foldLeft(S_new)(union)
      D_i += intersection(L, S_new)
    }

    new DeterministicDistanceBasedPlan(p, D_i.toArray)
  }

  /**
   * Finds those states from which the goal will be reached eventually.
   * See Rintanen script fig. 4.5.
   */
  def pruneStrongCyclic(operators: Iterable[PlanningDomainAction], state: Int, goal: Int)(implicit t: UniqueTable): Int = {
    import t._

    var W_new = state;
    var W = -1
    while (W_new != W) {
      W = W_new
      var S_new = goal // States from which goal can be reached in i steps
      var S = -1
      while (S_new != S) {
        S = S_new
        // S_k = S_(k-1) ∪  (∪_(o \in O) (wpreimg_o(S_(k-1)) ∩ spreimg_o(W_(i-1)))):
        S_new = operators.map(o => intersection(o.weakPreImage(S), o.strongPreImage(W))).foldLeft(S)(union)
      }
      W_new = intersection(W, S_new) // <- States that stay within W and eventually reach G
    }
    W_new // <- States in W_new that stay within W_new and eventually reach G
  }

  /** The pre-image function for weak plans.*/
  def weakPreImage(reachedStates: Int, p: PlanningProblem)(implicit t: UniqueTable) =
    {
      import t._
      p.actions.zipWithIndex.map {
        case (action, index) => {
          val weakPreImgSuitable = !isEmpty(intersection(action.weakPreImage(reachedStates), reachedStates))
          this.logger.debug("Comparing expression for action #" + index + "\n with effects " +
            structureOf(action.weakPreImage(reachedStates), p.variableNames).mkString("\n") +
            "\nwith current reachable state\n" + structureOf(reachedStates, p.variableNames).mkString("\n") +
            "accepted ? " + weakPreImgSuitable)
          if (weakPreImgSuitable) {
            Some((p.actions(index).precondition.id, index))
          } else None
        }
      }.flatten
    }

  /** The pre-image function for strong plans.*/
  def strongPreImage(reachedStates: Int, p: PlanningProblem)(implicit t: UniqueTable) =
    p.actions.map(_.strongPreImage(reachedStates)).zipWithIndex.filter(x => !t.isEmpty(x._1))

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