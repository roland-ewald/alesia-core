package alesia.results

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec
import alesia.planning.execution.PlanExecutor
import alesia.planning.plans.FullPlanExecutionResult
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Tests for [[ReportingResultAnalyzer]].
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestReportingResultAnalyzer extends FunSpec with ShouldMatchers {

  describe("Reporting result analyzer") {

    it("work on empty results") {
      pending
      val report = ReportingResultAnalyzer(FullPlanExecutionResult(Seq()))
    }

  }

}