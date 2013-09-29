package alesia.planning.planners.nondet

import scala.annotation.tailrec

import alesia.planning.plans.Plan
import sessl.util.Logging

/**
 * Represents a deterministic policy, in which for each state there is a single action to be executed.
 *
 * @author Roland Ewald
 */
case class PolicyPlan(val policy: NonDeterministicPolicy) extends Plan with Logging {
  require(policy.stateActionTable.nonEmpty)
  
  logger.info(s"Plan:\n\n${symbolicRepresentation}")

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