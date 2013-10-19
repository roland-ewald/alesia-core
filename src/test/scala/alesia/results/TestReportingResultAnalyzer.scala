package alesia.results

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec
import alesia.planning.execution.PlanExecutor
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import alesia.planning.scenarios.SimplePropertyCheckScenario
import alesia.query.UserDomainEntity
import alesia.query.TerminateWhen
import alesia.planning.execution.WallClockTimeMaximum

/**
 * Tests for [[ReportingResultAnalyzer]].
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestReportingResultAnalyzer extends FunSpec with ShouldMatchers {

  import SimplePropertyCheckScenario._

  val normalResult = alesia.query.submit {
    domain
  } {
    preferences: _*
  } {
    hypothesis
  }

  val failureResult = alesia.query.submit {
    domain
  } {
    TerminateWhen(WallClockTimeMaximum(milliseconds = 1))
  } {
    hypothesis
  }

  describe("Reporting result analyzer") {

    it("works on empty results") {
      val report = ReportingResultAnalyzer(FullPlanExecutionResult(Seq()))
      assert(report.failure)
      assert(report.failureCause == None)
    }

    it("works with failure results") {
      val report = ReportingResultAnalyzer(failureResult)
      assert(report.failure)
      assert(report.failureCause.isDefined)
    }

    it("produces action-by-action output") {
      val report = ReportingResultAnalyzer(normalResult)
      assert(!report.failure)
      assert(!report.failureCause.isDefined)
      report.actions.size should be(normalResult.numOfActions)
      report.actions foreach { ar =>
        assert(ar.index >= 0)
        assert(!ar.name.isEmpty)
      }
    }
  }

  describe("Result report renderer") {

    it("can render a normal report") {
      val report = ReportingResultAnalyzer(normalResult)
      pending
    }

  }

  //TODO: use result report renderer from JAMES II?

}