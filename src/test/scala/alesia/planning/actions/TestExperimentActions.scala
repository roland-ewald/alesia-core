package alesia.planning.actions

import scala.math.abs
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import alesia.ExperimentationTest
import alesia.planning.domain.Algorithm
import alesia.planning.actions.experiments.CalibrateSimSteps
import alesia.planning.actions.experiments.CheckQSSModelProperty
import alesia.planning.context.LocalJamesExecutionContext

/**
 * Tests for experiment actions.
 *  @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestExperimentActions extends ExperimentationTest {

  test("calibration") {
    val action = TestCalibrationSimSteps.action
    //    action.execute
    TestCalibrationSimSteps.checkResult(action)
  }

  test("qss-check") {
    val action = TestCheckQSSModelProperty.action
    action.execute(new LocalJamesExecutionContext(Seq(), Seq()))
  }

}

object TestCalibrationSimSteps extends ExperimentationTest {

  val desiredRuntime = 3.0

  val permEpsilon = 0.1

  def action = CalibrateSimSteps(problem, nrm, desiredRuntime, eps = permEpsilon)

  def checkResult(action: CalibrateSimSteps) = for (result <- action.resultFor(action.result)) result match {
    case (prob, sim, s, r) => {
      require(s.isInstanceOf[Long] && r.isInstanceOf[Double], "Wrong result type: " + s.getClass + " & " + r.getClass + " instead of Long & Double.")
      val steps = s.asInstanceOf[Long]
      val runtime = r.asInstanceOf[Double]
      logger.info("Calibration result: " + result)
      assert(steps > 1000, "More than 10000 steps should be required.")
      assert(abs(runtime - desiredRuntime) / desiredRuntime <= permEpsilon)
    }
  }
}

object TestCheckQSSModelProperty extends ExperimentationTest {

  def action = CheckQSSModelProperty(problem, nrm, 20.5)

}