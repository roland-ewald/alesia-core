package alesia.planning.execution.actors

import org.junit.Assert.assertNotNull
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import alesia.ExperimentationTest
import alesia.planning.actions.TestCalibrationSimSteps
import alesia.planning.execution.PlanExecutor
import alesia.planning.plans.SingleActionPlan
import alesia.planning.context.EmptyExecutionContext
import alesia.planning.plans.EmptyPlan
import alesia.planning.PlanningProblem
import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.actions.ActionDeclaration


/**
 * Test execution of a trivial plan.
 *  @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestTrivialPlanExecution extends ExperimentationTest {

  val dummyProblem = new DomainSpecificPlanningProblem() {
    val initialState = FalseVariable
    val goalState = TrueVariable
    val declaredActions = Map[Int, ActionDeclaration]()
    val planningActions = Map[Int, DomainAction]()
  }

  test("executing a single calibration action") {
    
    pending
    //FIXME
    
    val executor: PlanExecutor = PlanExecutionMaster(PlanExecutionSlave(10))
    val result = executor.execute((dummyProblem, SingleActionPlan(0), EmptyExecutionContext))
    assertNotNull(result)
  }

}