package alesia.planning.planners.nondet

import alesia.planning.PlanningProblem
import alesia.planning.plans.Plan
import alesia.planning.plans.EmptyPlan
import alesia.planning.actions.ExperimentAction
import alesia.planning.context.Context
import scala.annotation.tailrec
import alesia.utils.bdd.UniqueTable

/**
 * Represents a policy, as produced by a planner for non-deterministic domains.
 *
 * @author Roland Ewald
 */
sealed trait Policy extends Plan {

  /** The set of states covered by this policy (given as the instruction id of its characteristic function). */
  def states: Int = 0

  /** Joins to policies. */
  def ++(other: Policy) = other

  /** The symbolic representation of the policy. */
  val symbolicRepresentation: String
}

/**
 * Represents a non-deterministic policy, in which the states in which actions are executed may still overlap.
 */
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

  override def decide(c: Context): Seq[ExperimentAction] = throw new UnsupportedOperationException

  /**
   * Constructs a symbolic representation of the preconditions (as nested if statements) and the action they trigger if true.
   */
  override lazy val symbolicRepresentation: String = stateActionTable.map {
    case (state, action) =>
      problem.table.structureOf(state, problem.variableNames, "  ").mkString("\n") +
        " => " + problem.actions(action).name + "\n===="
  }.mkString("\n")
}

/**
 * Represents a deterministic policy, in which for each state there is a single action to be executed.
 */
case class DeterministicPolicyPlan(val policy: NonDeterministicPolicy) extends Plan {
  require(policy.stateActionTable.nonEmpty)

  //TODO: Merge this with the other decision method
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

  lazy val symbolicRepresentation = policy.symbolicRepresentation
}

/** Represents a failure to find a plan. */
case object FailurePolicy extends Policy with EmptyPlan {
  override val symbolicRepresentation = "Failure"
}

/** Represents the trivial policy.*/
case object EmptyPolicy extends Policy with EmptyPlan {
  override val symbolicRepresentation = "Empty"
}

object Policy {
  
  /** Creates a universal policy. Formally, ... */
  def universal(p: PlanningProblem)(implicit t: UniqueTable) =
    NonDeterministicPolicy(p, p.actions.map(a => (a.precondition.id, a.effect)).toMap, t.emptySet)
}
