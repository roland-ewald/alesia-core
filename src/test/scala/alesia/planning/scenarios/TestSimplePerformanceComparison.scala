package alesia.planning.scenarios

import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import alesia.TestUtils._
import alesia.query._
import alesia.results.FailurePlanExecutionResult
import alesia.results.DefaultResultRenderer
import alesia.results.ReportingResultAnalyzer
import java.io.File

/**
 * Tests for a simple scenario that aims to compare the performance of two
 * simulation algorithms on some calibrated benchmark model.
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestSimplePerformanceComparison extends FunSpec with ShouldMatchers {

  describe("Simple performance comparison scenario") {
    it("works in principle") {
      val result = submit(SimplePerformanceComparisonScenario)
      val report = ReportingResultAnalyzer(result)
      DefaultResultRenderer.storeReport(report, "Simple Comparison Scenario", new File("./simple-comparison"))
      result should be(ofType[FailurePlanExecutionResult])
      result.trace.size should equal(SimplePerformanceComparisonScenario.maxActionNumber + 1)
    }
  }

}
