package alesia.planning.planners

import alesia.planning.planners.Plan
import alesia.planning.PlanningProblem



/** Interface for planning algorithms.
 *  @author Roland Ewald
 */
trait Planner {

  def plan(problem: PlanningProblem): Plan
  
}