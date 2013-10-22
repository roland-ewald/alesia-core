package alesia.results

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import sessl.util.MiscUtils
import java.io.File

/**
 * Tests for [[alesia.results.DefaultResultRenderer]].
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestDefaultResultRenderer extends FunSpec with ShouldMatchers {

  import TestScenarios._

  describe("The default result renderer") {

    it("can render a report from a failure result") {
      checkRendererCreatesResult(ReportingResultAnalyzer(failureResult), "./test-failure")
    }

    it("can render a report from a normal result") {
      checkRendererCreatesResult(ReportingResultAnalyzer(normalResult), "./test-normal")
    }
  }

  def checkRendererCreatesResult(report: PlanExecutionReport, dirName: String): Unit = {
    val testTarget = new File(dirName)
    MiscUtils.deleteRecursively(testTarget)
    DefaultResultRenderer.storeReport(report, "Test Scenario", testTarget)
    testTarget.exists should be(true)
    testTarget.list.size should be >= 1
  }

}