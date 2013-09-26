package alesia.planning.scenarios

import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import alesia.planning.plans.FailurePlanExecutionResult
import org.junit.Assert._

/**
 * Tests for a simple scenario that aims to compare the performance of two
 * simulation algorithms on some calibrated benchmark model.
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class SimpleComparison extends FunSpec {

  describe("Simple Comparison Scenario") {

    it("works in principle :)") {

      import alesia.query._

      pending

      val execResults = submit {
        SingleModel("java://examples.sr.LinearChainSystem")
      } {
        WallClockTimeMaximum(seconds = 30)
      } {
        exists >> model | hasProperty("qss")
      }

      assertNotNull(execResults)
      assertFalse(execResults.isInstanceOf[FailurePlanExecutionResult])
      assertTrue(execResults.trace.length > 0)
    }

    //TODO: Add plan execution that fails, check for FailurePlanExecutionResult

    it("fails whenever elements of the problem specification are missing") {
      pending
    }

    it("can benefit from previously achieved results") {
      pending
    }

    it("can be executed remotely") {
      pending
    }
  }
}

/**
 * Contains all user-defined elements for this scenario.
 */
object SimpleComparison {

  //TODO: Implement simple mechanism to transform hypothesis into goal
  //TODO: Implement & test necessary actions
  //TODO: Implement prototype of execution infrastructure / plan monitor

}