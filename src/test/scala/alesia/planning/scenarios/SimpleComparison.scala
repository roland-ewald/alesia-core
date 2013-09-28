package alesia.planning.scenarios

import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import alesia.planning.plans.FailurePlanExecutionResult
import org.junit.Assert._
import org.jamesii.core.util.logging.ApplicationLogger
import java.util.logging.Level
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.matchers.BePropertyMatchResult
import org.scalatest.matchers.BePropertyMatcher

import alesia.TestUtils._

/**
 * Tests for a simple scenario that aims to compare the performance of two
 * simulation algorithms on some calibrated benchmark model.
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class SimpleComparison extends FunSpec with ShouldMatchers {

  ApplicationLogger.setLogLevel(Level.SEVERE)

  describe("Simple Property Check Scenario") {

    import alesia.query._

    it("works in principle :)") {

      val results = submit {
        SingleModel("java://examples.sr.LinearChainSystem")
      } {
        WallClockTimeMaximum(seconds = 30)
      } {
        exists >> model | hasProperty("qss")
      }

      results.trace.size should be >= 2
      results.trace.size should be <= 3
      results should not be ofType[FailurePlanExecutionResult]
    }

    it("fails whenever elements of the problem specification are missing") {
      evaluating {
        submit {
          new UserDomainEntity {}
        } {
          WallClockTimeMaximum(seconds = 30)
        } {
          exists >> model | hasProperty("qss")
        }
      } should produce[IllegalArgumentException]
    }

    it("can be executed remotely") {
      pending
    }

    it("can benefit from previously achieved results") {
      //TODO: hand over old state / user domain entities?
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