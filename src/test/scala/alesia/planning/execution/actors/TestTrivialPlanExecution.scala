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

/**
 * Test execution of a trivial plan.
 *  @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestTrivialPlanExecution extends ExperimentationTest {

  val dummyProblem = new PlanningProblem() {
    val initialState = FalseVariable
    val goalState = TrueVariable
  }

  test("executing a single calibration action") {
    
    pending
    //FIXME
    
    val executor: PlanExecutor = PlanExecutionMaster(PlanExecutionSlave(10))
    val result = executor.execute((dummyProblem, SingleActionPlan(0), EmptyExecutionContext))
    assertNotNull(result)
  }

}