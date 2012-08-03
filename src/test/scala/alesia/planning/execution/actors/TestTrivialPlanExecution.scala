package alesia.planning.execution.actors

import org.junit.Test
import org.junit.Assert._

import alesia.planning.execution.PlanExecutor
import alesia.ExperimentationTest
import alesia.planning.plans.SingleActionPlan
import alesia.planning.actions.TestCalibrationSimSteps

/** Test execution of a trivial plan.
 *  @author Roland Ewald
 */
@Test
class TestTrivialPlanExecution extends ExperimentationTest {

  @Test
  def testOneElementCalibrationPlan() = {
    val executor: PlanExecutor = PlanExecutionMaster(PlanExecutionSlave(10))
    val result = executor.execute(SingleActionPlan(TestCalibrationSimSteps.action))
    assertNotNull(result)
  }

}