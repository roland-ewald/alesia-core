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
                                                  //| lesia.planning.planners.nondet.NonDeterministicPolicyPlanner@4de77a3e
  implicit val ut = problem.table                 //> ut  : alesia.utils.bdd.UniqueTable = alesia.utils.bdd.UniqueTable@180aa467

  val preImage = planner.strongPreImage(problem.goalState.id, problem)
                                                  //> 4 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner 
                                                  //| - Evaluating action 'wait-at-light'...
                                                  //| 10 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Evaluating action 'drive-truck'...
                                                  //| 13 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Action 'drive-truck' is applicable.
                                                  //| 13 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Evaluating action 'drive-truck-back'...
                                                  //| 13 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Evaluating action 'make-fuel'...
                                                  //| 14 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Evaluating action 'air-truck-transit'...
                                                  //| preImage  : Array[(Int, Int)] = Array((24,1))

  ut.structureOf(preImage(0)._1, problem.variableNames).mkString("\n")
                                                  //> res0: String = if(fuel) {
                                                  //| 	if(trafficJam) {
                                                  //| 		if(pos=truck-station) {
                                                  //| 			true
                                                  //| 		}
                                                  //| 	}
                                                  //| 	else {
                                                  //| 		if(pos=city-center) {
                                                  //| 			true
                                                  //| 		}
                                                  //| 		else {
                                                  //| 			if(pos=truck-station) {
                                                  //| 				true
                                                  //| 			}
                                                  //| 		}
                                                  //| 	}
                                                  //| }

  new SamplePlanningProblemTransport().table.structureOf(24).mkString("\n")
                                                  //> res1: String = if(24) {
                                                  //| 	if(23) {
                                                  //| 		if(10) {
                                                  //| 			true
                                                  //| 		}
                                                  //| 	}
                                                  //| 	else {
                                                  //| 		if(22) {
                                                  //| 			true
                                                  //| 		}
                                                  //| 		else {
                                                  //| 			if(10) {
                                                  //| 				true
                                                  //| 			}
                                                  //| 		}
                                                  //| 	}
                                                  //| }

}