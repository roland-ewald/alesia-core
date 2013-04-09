package alesia.planning.actions

import scala.math.abs
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import alesia.ExperimentationTest
import alesia.planning.domain.Algorithm
import alesia.planning.actions.experiments.CalibrateSimSteps

/**
 * Tests for experiment actions.
 *  @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestExperimentActions extends ExperimentationTest {

  test("calibration") {
    val action = TestCalibrationSimSteps.action
    TestCalibrationSimSteps.checkResult(action)
  }

}

object TestCalibrationSimSteps extends ExperimentationTest {

  val desiredRuntime = 3.0

  val permEpsilon = 0.1

  def action = CalibrateSimSteps(problem, Algorithm(sessl.james.NextReactionMethod()), desiredRuntime, eps = permEpsilon)

  def checkResult(action: CalibrateSimSteps) = for (result <- action.resultFor(action.result)) result match {
    case (s, r) => {
      require(s.isInstanceOf[Long] && r.isInstanceOf[Double], "Wrong result type: " + s.getClass + " & " + r.getClass + " instead of Long & Double.")
      val steps = s.asInstanceOf[Long]
      val runtime = r.asInstanceOf[Double]
      logger.info("Calibration result: " + result)
      assert(steps > 1000, "More than 10000 steps should be required.")
      assert(abs(runtime - desiredRuntime) / desiredRuntime <= permEpsilon)
    }
  }
}