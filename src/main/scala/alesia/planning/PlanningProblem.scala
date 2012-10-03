package alesia.planning

/**
 * Represents a planning problem.
 *
 * @author Roland Ewald
 */
abstract case class PlanningProblem() extends PlanningDomain {

  /** Initial state. */
  val initialState: PlanningDomainVariable

  /** Goal state. */
  val goalState: PlanningDomainVariable

  /** Instruction id for function that characterizes initial state. */
  def initialStateId = initialState.id

  /** Instruction id for function that characterizes goal state. */
  def goalStateId = goalState.id
}