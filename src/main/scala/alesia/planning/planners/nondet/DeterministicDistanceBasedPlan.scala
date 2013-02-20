package alesia.planning.planners.nondet

import alesia.planning.plans.Plan
import alesia.planning.context.Context
import alesia.planning.PlanningProblem

/**
 * 
 * 
 * @author Roland Ewald
 */
class DeterministicDistanceBasedPlan(val p:PlanningProblem, val distances: Array[Int]) extends Plan {

  override def decide(c: Context) = Seq()
  
  override def decide(c: Int) = {
    
  Seq()
  }
  
  
}