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

/**
 * Tests for a simple scenario that aims to compare the performance of two
 * simulation algorithms on some calibrated benchmark model.
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class SimpleComparison extends FunSpec with ShouldMatchers {

  ApplicationLogger.setLogLevel(Level.SEVERE)

  describe("Simple Comparison Scenario") {

    it("works in principle :)") {

      import alesia.query._

      val execResults = submit {
        SingleModel("java://examples.sr.LinearChainSystem")
      } {
        WallClockTimeMaximum(seconds = 30)
      } {
        exists >> model | hasProperty("qss")
      }

      execResults should not be (null)
      execResults.trace.size should be >= 2
      execResults.trace.size should be <= 3
      execResults should not be(anInstanceOf[FailurePlanExecutionResult])
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

  /** Originally from Bill Venners: https://groups.google.com/d/msg/scalatest-users/UrdRM6XHB4Y/dpOzl3iSxqoJ . */
  def anInstanceOf[T](implicit m: Manifest[T]) = {
    val c = m.runtimeClass.asInstanceOf[Class[T]]
    new BePropertyMatcher[AnyRef] {
      def apply(left: AnyRef) =
        BePropertyMatchResult(c.isAssignableFrom(left.getClass), s"an instance of ${c.getName}")
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