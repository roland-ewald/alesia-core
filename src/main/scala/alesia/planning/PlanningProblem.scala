package alesia.planning

/**
 * Represents a planning problem.
 *
 * @author Roland Ewald
 */
abstract case class PlanningProblem() extends PlanningDomain {

  /** Instruction id for function that characterizes initial state. */
  val initialState: PlanningDomainVariable

  /** Instruction id for function that characterizes goal state. */
  val goalState: PlanningDomainVariable

}