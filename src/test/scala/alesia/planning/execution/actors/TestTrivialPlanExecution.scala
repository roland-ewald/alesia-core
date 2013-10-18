package alesia.planning.execution.actors

import org.junit.Assert.assertNotNull
import org.junit.runner.RunWith
import alesia.ExperimentationTest
import alesia.planning.execution.PlanExecutor
import alesia.planning.execution.ExecutionState
import org.scalatest.junit.JUnitRunner
import alesia.planning.DummyDomainSpecificPlanningProblem
import alesia.planning.planners.SingleActionPlan
import alesia.planning.context.LocalJamesExecutionContext

/**
 * Test execution of a trivial plan.
 *  @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestTrivialPlanExecution extends ExperimentationTest {

  test("executing a single calibration action") {

    pending
    //FIXME

    val executor: PlanExecutor = PlanExecutionMaster(PlanExecutionSlave(10))
    val result = executor(ExecutionState(DummyDomainSpecificPlanningProblem, SingleActionPlan(0), LocalJamesExecutionContext()))
    assertNotNull(result)
  }

}