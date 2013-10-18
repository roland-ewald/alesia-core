package alesia.planning.planners.nondet

import alesia.planning.planners.Plan
import alesia.planning.context.ExecutionContext
import alesia.planning.PlanningProblem

/**
 * Plan that is based on a list of state sets, ordered by their distance to the goal states
 * (the set of goals is the first element of the array).
 *
 * @author Roland Ewald
 */
class DistanceBasedPlan(val problem: PlanningProblem, val distances: Array[Int]) extends Plan {
  import problem.table._

  /** Check how far away from the goal the state is */
  override def decide(state: Int) = {

    val distanceIndex = distances.indexWhere(isContained(state, _))

    if (distanceIndex <= 0)
      Seq()
    else
      suitableActions(state, distanceIndex)
  }

  /** Find actions that move the state towards the goal. */
  def suitableActions(state: Int, distIndex: Int) = {
    problem.actions.zipWithIndex.filter { a =>
      isContained(state, a._1.weakPreImage(distances(distIndex - 1)))
      //      isContained(preImg, distances(distIndex)) && !isContained(preImg, distances(distIndex - 1))
    } map (_._2)
  }

}