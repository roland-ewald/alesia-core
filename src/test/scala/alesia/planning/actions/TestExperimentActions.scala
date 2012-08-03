package alesia.planning.actions

import org.junit.Assert.assertTrue
import org.junit.Test
import alesia.bindings.james.JamesExperimentProvider
import alesia.planning.domain.Algorithm
import alesia.planning.domain.Problem
import examples.sr.LinearChainSystem
import sessl.util.Logging
import scala.math._
import alesia.ExperimentationTest
import alesia.ExperimentationTest

/** Tests for experiment actions.
 *  @author Roland Ewald
 */
class TestExperimentActions extends ExperimentationTest {

  @Test
  def testCalibration = {
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
      assertTrue("More than 10000 steps should be required.", steps > 1000)
      assertTrue("", abs(runtime - desiredRuntime) / desiredRuntime <= permEpsilon)
    }
  }

}