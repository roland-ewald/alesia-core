package alesia.planning

/**
 * Trivial planning problem for testing.
 * 
 * @author Roland Ewald
 *
 */
class TrivialPlanningProblem extends PlanningProblem {
  
  val solvable = v("solvable")
  val solved = v("solved")
  
  val initialState = solvable
  val goalState = solved
  
}