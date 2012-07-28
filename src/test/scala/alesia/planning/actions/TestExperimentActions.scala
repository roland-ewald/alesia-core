package alesia.planning.actions

import org.junit.Assert.assertTrue
import org.junit.Test

import alesia.bindings.james.JamesExperimentProvider
import alesia.planning.domain.Algorithm
import alesia.planning.domain.Problem
import examples.sr.LinearChainSystem
import sessl.util.Logging

import scala.math._

/** Tests for experiment actions.
 *  @author Roland Ewald
 */
@Test
class TestExperimentActions extends Logging {

  /** The experiment provider. */
  implicit val expProvider = JamesExperimentProvider

  /** The test problem. */
  val problem = Problem("java://" + classOf[LinearChainSystem].getName)

  @Test
  def testCalibration = {
    val desiredRuntime = 3.0
    val permEpsilon = 0.1
    val c = CalibrateSimSteps(problem, Algorithm(sessl.james.NextReactionMethod()), desiredRuntime, eps = permEpsilon)
    c.execute
    for (result <- c.resultFor(c.result)) result match {
      case r: (Long, Double) => {
        logger.info("Calibration result: " + result)
        assertTrue("More than 10000 steps should be required.", r._1 > 1000)
        assertTrue("", abs(r._2 - desiredRuntime) / desiredRuntime <= permEpsilon)
      }
    }
  }

}