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
import alesia.planning.execution.StateUpdate
import alesia.planning.actions.experiments.QSSCheckResults
import alesia.query.SingleSimulator
import alesia.planning.actions.experiments.CompareSimulators
import alesia.planning.context.LocalJamesExecutionContext
import alesia.planning.actions.experiments.ComparisonResults

/**
 * Tests for experiment actions.
 *  @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestExperimentActions extends ExperimentationTest with ShouldMatchers {

  val desiredRuntime = 0.1

  describe("Calibration action") {
    it("works in principle") {
      for (result <- calibration.results) {
        result.steps should be >= 1000L
        (abs(result.runtime - desiredRuntime) / desiredRuntime) should be <= 0.3
      }
    }
  }

  describe("Quasi-Steady-State Checking Action") {
    it("works in principle") {
      val linearSteps = 4
      val action = CheckQSSModelProperty(calibration, 20, 0.4, linearSteps)
      val update = action.execute(LocalJamesExecutionContext())

      val results = update.addedEntities.collect { case q: QSSCheckResults => q }
      results.size should equal(1)
      results.head.results.size should equal(1)

      val checkResult = results.head.results.head
      checkResult.runtimes.size should equal(linearSteps)
      checkResult.slowDownRatioErrors.size should equal(linearSteps)
      checkResult.runtimes.foreach(r => r._2 should be > 0.0)
    }
  }

  describe("Comparison Action") {

    import SharedLiterals._

    val significanceLevel = 0.1
    val replicationNumber = 21

    it("works in principle") {
      val calibrationResults = CalibrationResults(Seq(calibration.results.head, calibrate(dm).results.head))
      val context = LocalJamesExecutionContext(entitiesForLiterals = Map(calibratedModel -> Seq(calibrationResults)))
      val update = CompareSimulators(nrm, dm, replicationNumber, significanceLevel).execute(context)

      val results = update.addedEntities.collect { case c: ComparisonResults => c }
      results.nonEmpty should be(true)

      if (results.head.pValue < significanceLevel)
        results.size should be(1)
      else {
        results.size should be(2)
        results(0) should equal(results(1))
      }

      results.head.pValue should (be >= 0.0 and be <= 1.0)

      for (r <- Seq(results.head.resultsA, results.head.resultsB)) {
        r.size should be(replicationNumber)
        r.forall(_ > 0) should be(true)
      }
    }
  }

  lazy val calibration: CalibrationResults = calibrate(nrm)

  def calibrate(s: SingleSimulator) = {
    val action = CalibrateSimSteps(problem, Seq(s), desiredRuntime, eps = 0.4)
    val update = action.execute(LocalJamesExecutionContext())
    update.addedEntities.collect { case c: CalibrationResults => c }.head
  }

}
