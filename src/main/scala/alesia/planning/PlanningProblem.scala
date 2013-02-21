package alesia.planning

import sessl.util.Logging

/**
 * Represents a planning problem.
 *
 * @author Roland Ewald
 */
abstract case class PlanningProblem() extends PlanningDomain {

  /** Function to characterize set of initial states. */
  val initialState: PlanningDomainFunction

  /** Function to characterize set of goal states. */
  val goalState: PlanningDomainFunction

  /** Instruction id for function that characterizes initial state. */
  lazy val initialStates = initialState.id

  /** Instruction id for function that characterizes goal state. */
  lazy val goalStates = goalState.id

}