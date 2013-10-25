package alesia.planning.actions

import scala.math.abs
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import alesia.ExperimentationTest
import alesia.planning.domain.Algorithm
import alesia.planning.actions.experiments.CalibrateSimSteps
import alesia.planning.actions.experiments.CheckQSSModelProperty
import alesia.planning.context.LocalJamesExecutionContext
import alesia.planning.context.ExecutionStatistics
import alesia.planning.actions.experiments.CalibrationResults
import org.scalatest.matchers.ShouldMatchers

/**
 * Tests for experiment actions.
 *  @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestExperimentActions extends ExperimentationTest with ShouldMatchers {

  val desiredRuntime = 3.0

  val permEpsilon = 0.1

  describe("Calibration action") {
    it("works in principle") {
      val action = CalibrateSimSteps(problem, Seq(nrm), desiredRuntime, eps = permEpsilon)
      val update = action.execute(LocalJamesExecutionContext())
      update.addLinks.map(_._2).collect {
        case r: CalibrationResults =>
          for (result <- r.results) {
            result.steps should be >= 1000L
            (abs(result.runtime - desiredRuntime) / desiredRuntime) should be <= permEpsilon
          }
      }
    }
  }

  describe("QSS Action") {
    it("works in principle") {
      val action = CheckQSSModelProperty(problem, nrm, 20.5)
      action.execute(LocalJamesExecutionContext())
    }
  }

}
