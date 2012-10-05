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
                                                  //| lesia.planning.planners.nondet.NonDeterministicPolicyPlanner@7bfdc405
  implicit val ut = problem.table                 //> ut  : alesia.utils.bdd.UniqueTable = alesia.utils.bdd.UniqueTable@515063db

  val preImage = planner.strongPreImage(problem.goalState.id, problem)
                                                  //> 5 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner 
                                                  //| - Evaluating action 'drive-train'...
                                                  //| 16 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Action 'drive-train' is applicable.
                                                  //| 16 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Evaluating action 'wait-at-light'...
                                                  //| 16 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Evaluating action 'drive-truck'...
                                                  //| 17 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Action 'drive-truck' is applicable.
                                                  //| 17 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Evaluating action 'drive-truck-back'...
                                                  //| 18 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Evaluating action 'make-fuel'...
                                                  //| 19 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Evaluating action 'fly'...
                                                  //| 20 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Action 'fly' is applicable.
                                                  //| 20 [main] INFO alesia.planning.planners.nondet.NonDeterministicPolicyPlanner
                                                  //|  - Evaluating action 'air-truck-transit'...
                                                  //| preImage  : Array[(Int, Int)] = Array((20,0), (27,2), (32,5))

  ut.structureOf(preImage(0)._1, problem.variableNames).mkString("\n")
                                                  //> res0: String = if(light=green) {
                                                  //| 	if(pos=train-station) {
                                                  //| 		true
                                                  //| 	}
                                                  //| 	else {
                                                  //| 		if(pos=Victoria-station) {
                                                  //| 			true
                                                  //| 		}
                                                  //| 	}
                                                  //| }
                                                  //| else {
                                                  //| 	if(pos=train-station) {
                                                  //| 		true
                                                  //| 	}
                                                  //| }

  new SamplePlanningProblemTransport().table.structureOf(24).mkString("\n")
                                                  //> res1: String = if(24) {
                                                  //| 	if(!23) {
                                                  //| 		if(9) {
                                                  //| 			true
                                                  //| 		}
                                                  //| 	}
                                                  //| }

}