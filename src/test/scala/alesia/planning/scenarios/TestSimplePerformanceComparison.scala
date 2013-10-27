package alesia.planning.scenarios

import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import alesia.TestUtils._
import alesia.query._
import alesia.results.FailurePlanExecutionResult

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
      //      pending
      val result = submit(SimplePerformanceComparisonScenario)
      result should not be ofType[FailurePlanExecutionResult]
    }

  }

}
