package alesia.planning.planners.nondet

import alesia.planning.SamplePlanningProblemTransport
import alesia.planning.planners.nondet._

/**
 * Helper for interactive debugging.
 *
 * @author Roland Ewald
 */
object NonDetPlannerPlayground {

  val problem = new SamplePlanningProblemTransport//> problem  : alesia.planning.SamplePlanningProblemTransport = PlanningProblem(
                                                  //| )
  val planner = new NonDeterministicPolicyPlanner()
                                                  //> planner  : alesia.planning.planners.nondet.NonDeterministicPolicyPlanner = a
                                                  //| lesia.planning.planners.nondet.NonDeterministicPolicyPlanner@d1c5bb0
  implicit val ut = problem.table                 //> ut  : alesia.utils.bdd.UniqueTable = alesia.utils.bdd.UniqueTable@40c78689

  val preImage = planner.strongPreImage(problem.goalState.id, problem)
                                                  //> preImage  : Array[(Int, Int)] = Array((381,0), (12,1), (744,2), (870,3), (94
                                                  //| 0,4), (1409,5), (1552,6))

  ut.structureOf(preImage(0)._1, problem.variableNames).mkString("\n")
                                                  //> res0: String = if(light=green) {
                                                  //| 	if(pos=train-station) {
                                                  //| 		if(!pos=Victoria-station) {
                                                  //| 			if(pos=Gatwick) {
                                                  //| 				true
                                                  //| 			}
                                                  //| 		}
                                                  //| 	}
                                                  //| 	else {
                                                  //| 		if(pos=Victoria-station) {
                                                  //| 			true
                                                  //| 		}
                                                  //| 	}
                                                  //| }
                                                  //| else {
                                                  //| 	if(pos=train-station) {
                                                  //| 		if(pos=Gatwick) {
                                                  //| 			true
                                                  //| 		}
                                                  //| 	}
                                                  //| }

  new SamplePlanningProblemTransport().table.structureOf(24).mkString("\n")
                                                  //> res1: String = if(24) {
                                                  //| 	true
                                                  //| }
                                                  //| else {
                                                  //| 	if(20) {
                                                  //| 		true
                                                  //| 	}
                                                  //| }
  new DeterministicDistanceBasedPlan(problem, Array[Int]())
                                                  //> res2: alesia.planning.planners.nondet.DeterministicDistanceBasedPlan = alesi
                                                  //| a.planning.planners.nondet.DeterministicDistanceBasedPlan@3c44899b

}