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

  def plan(problem: PlanningProblem) = {
    //TODO: this finds strong plans, extend/generalize toward strong-cyclic and weak plans

    implicit val domain = problem.table
    val initialState = problem.initialStateId //S_0
    val goalState = problem.goalStateId //S_g

    var previousPolicy: Policy = FailurePolicy //π
    var currentPolicy: Policy = EmptyPolicy //π'    
    var reached = domain.union(goalState, currentPolicy.states) // S_π'∪ S_g
    var counter = 0

    // while π != π' and S_0 ⊈ S_π'∪ S_g  
    while (previousPolicy != currentPolicy && !domain.isContained(initialState, reached)) {

      logger.info("ITERATION " + counter)
      counter += 1

      val preImage = strongPreImage(reached, problem)

      // Create π"
      val newPolicy = pruneStates(preImage, reached, problem)

      // π <- π'
      previousPolicy = currentPolicy

      // π' <- π ∪ π"
      currentPolicy = currentPolicy ++ newPolicy

      if (currentPolicy.isInstanceOf[NonDeterministicPolicy])
        logger.info("current policy:\n" + DeterministicPolicy(currentPolicy.asInstanceOf[NonDeterministicPolicy]).symbolicRepresentation)

      // update S_π'∪ S_g
      reached = domain.union(goalState, currentPolicy.states)
    }

    if (domain.isContained(initialState, reached))
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
          //TODO: check correctness, make more efficient (by doing the work once, in the effects), deal with non-determinism          
          if (tab.isContained(effect, reachedStates)) {
            logger.info("Action '" + action.name + "' is applicable.")
            Some((problem.actions(index).precondition.id, index))
          } else None
        }
    }.flatten

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

  def makeDeterministic(policy: Policy): Plan = policy match {
    case EmptyPolicy => new EmptyPlan {}
    case FailurePolicy => FailurePolicy
    case pol: NonDeterministicPolicy => DeterministicPolicy(pol)
  }
}

sealed trait Policy extends Plan {
  def states: Int = 0
  def ++(other: Policy) = other
}

case object FailurePolicy extends Policy with EmptyPlan
case object EmptyPolicy extends Policy with EmptyPlan

case class NonDeterministicPolicy(val problem: PlanningProblem, val stateActionTable: Map[Int, Int], val reachedStates: Int) extends Policy {

  override val states = stateActionTable.keys.foldLeft(reachedStates)((s1, s2) => problem.table.union(s1, s2))

  override def ++(other: Policy) = other match {
    case EmptyPolicy => this
    case FailurePolicy => FailurePolicy
    case pol: NonDeterministicPolicy => {
      require(pol.problem == problem, "Policies to be joined must refer to the same problem domain.")
      new NonDeterministicPolicy(problem, stateActionTable ++ pol.stateActionTable, problem.table.union(states, pol.states))
    }
  }
  override def decide(c: Context): Seq[ExperimentAction] = Seq()
}

case class DeterministicPolicy(val policy: NonDeterministicPolicy) extends Plan {
  require(policy.stateActionTable.nonEmpty)

  def decide(c: Context): Seq[ExperimentAction] = Seq()

  /**
   * Decides upon an action, chooses the first one of which the preconditions are fulfilled.
   * TODO: This strategy needs to be refined, otherwise one may end up within an infinite loop
   */
  def decide(state: Int): Int = {
    @tailrec
    def decideFor(currentState: Int, stateActionPair: (Int, Int), stateActionPairs: Iterator[(Int, Int)]): Int = {
      if (policy.problem.table.isContained(stateActionPair._1, currentState))
        stateActionPair._2
      else if (stateActionPairs.isEmpty)
        -1
      else
        decideFor(currentState, stateActionPairs.next, stateActionPairs)
    }
    val stateActionPairs = policy.stateActionTable.iterator
    decideFor(state, stateActionPairs.next, stateActionPairs)
  }

  /**
   * Constructs a symbolic representation of the nested if statements and the action they trigger if true.
   */
  lazy val symbolicRepresentation: String = policy.stateActionTable.map {
    case (state, action) =>
      policy.problem.table.structureOf(state, policy.problem.variableNames, "  ").mkString("\n") +
        " => " + policy.problem.actions(action).name + "\n===="
  }.mkString("\n")
}