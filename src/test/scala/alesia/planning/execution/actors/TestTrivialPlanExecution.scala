package alesia.planning.execution.actors

import org.junit.Assert.assertNotNull
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import alesia.ExperimentationTest
import alesia.planning.actions.TestCalibrationSimSteps
import alesia.planning.execution.PlanExecutor
import alesia.planning.plans.SingleActionPlan
import alesia.planning.context.EmptyContext

/**
 * Test execution of a trivial plan.
 *  @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestTrivialPlanExecution extends ExperimentationTest {

  test("executing a single calibration action") {
    val executor: PlanExecutor = PlanExecutionMaster(PlanExecutionSlave(10))
    val result = executor.execute(SingleActionPlan(TestCalibrationSimSteps.action), EmptyContext)
    assertNotNull(result)
  }

}