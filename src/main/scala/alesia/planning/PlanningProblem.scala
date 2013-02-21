package alesia.planning

import sessl.util.Logging

/**
 * Represents a planning problem.
 *
 * @author Roland Ewald
 */
abstract case class PlanningProblem() extends PlanningDomain with Logging {

  /** Function to characterize set of initial states. */
  val initialState: PlanningDomainFunction

  /** Function to characterize set of goal states. */
  val goalState: PlanningDomainFunction

  /** Instruction id for function that characterizes initial state. */
  lazy val initialStates = initialState.id

  /** Instruction id for function that characterizes goal state. */
  lazy val goalStates = goalState.id

  /**
   * Helper method for debugging.
   * @param f boolean function to show
   * @param name name of the function to display
   */
  def debug(f: Int, name: String) = logger.debug(name + ":\n" + table.structureOf(f, variableNames).mkString("\n"))
}