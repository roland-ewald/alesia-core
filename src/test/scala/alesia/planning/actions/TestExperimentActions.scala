package alesia.planning.actions

import org.junit.Test
import org.junit.Assert._
import alesia.bindings.james.JamesExperimentProvider
import alesia.planning.data.Problem
import examples.sr.LinearChainSystem
import sessl.util.Logging

/**
 * @author Roland Ewald
 */
@Test
class TestExperimentActions extends Logging {

  /** The experiment provider. */
  implicit val expProvider = JamesExperimentProvider

  /** The test problem. */
  val problem = Problem("java://" + classOf[LinearChainSystem].getName)

  @Test
  def testCalibration = {
    val result = CalibrateSimSteps(problem, 3.0)
    logger.info("Calibration result: " + result)
    assertTrue("More than 1000 steps should be required.", result._1 > 1000)
  }

}