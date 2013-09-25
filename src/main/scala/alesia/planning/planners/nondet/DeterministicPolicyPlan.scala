package alesia.planning.planners.nondet

import alesia.planning.plans.Plan
import alesia.planning.actions.experiments.ExperimentAction
import alesia.planning.context.ExecutionContext
import scala.annotation.tailrec

/**
 * Represents a deterministic policy, in which for each state there is a single action to be executed.
 *
 * @author Roland Ewald
 */
case class DeterministicPolicyPlan(val policy: NonDeterministicPolicy) extends Plan {
  require(policy.stateActionTable.nonEmpty)
  
  println(symbolicRepresentation)

  /**
   * Decides upon an action, chooses the first one of which the preconditions are fulfilled.
   * TODO: This strategy needs to be refined, otherwise one may end up within an infinite loop
   */
  override def decide(state: Int): Iterable[Int] = {
    @tailrec
    def decideFor(currentState: Int, stateActionPair: (Int, Int), stateActionPairs: Iterator[(Int, Int)]): Int = {
      if (policy.problem.table.isContained(currentState, stateActionPair._1))
        stateActionPair._2
      else if (stateActionPairs.isEmpty)
        throw new IllegalStateException(s"No match for state ${state}")
      else
        decideFor(currentState, stateActionPairs.next, stateActionPairs)
    }
    val stateActionPairs = policy.stateActionTable.iterator
    List(decideFor(state, stateActionPairs.next, stateActionPairs))
  }

  lazy val symbolicRepresentation = policy.symbolicRepresentation
}