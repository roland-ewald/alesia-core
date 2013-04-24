package alesia.planning.planners.nondet

import alesia.planning.PlanningProblem
import alesia.planning.plans.Plan
import alesia.planning.plans.EmptyPlan
import alesia.planning.actions.experiments.ExperimentAction
import alesia.planning.context.ExecutionContext
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

  override def decide(c: ExecutionContext): Seq[ExperimentAction] = throw new UnsupportedOperationException

  /**
   * Constructs a symbolic representation of the preconditions (as nested if statements) and the action they trigger if true.
   */
  override lazy val symbolicRepresentation: String = stateActionTable.map {
    case (state, action) =>
      problem.table.structureOf(state, problem.variableNames, "  ").mkString("\n") +
        " => " + problem.actions(action).name + "\n===="
  }.mkString("\n")
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
    NonDeterministicPolicy(p, p.actions.zipWithIndex.map(a => (a._1.precondition.id, a._2)).toMap, t.emptySet)
}
