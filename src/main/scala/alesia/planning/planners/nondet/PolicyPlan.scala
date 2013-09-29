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

  override def decide(state: Int): Iterable[Int] =
    policy.stateActionTable.filter(x => policy.problem.table.isContained(state, x._1)).map(_._2)

  lazy val symbolicRepresentation = policy.symbolicRepresentation
}