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
import alesia.query._
import alesia.planning.execution.FirstActionSelector
import alesia.planning.execution.DeterministicFirstActionSelector

/**
 * Tests a simple scenario where a benchmark model should be checked regarding a single property.
 *
 * It merely involves [[SingleModelIntroductionSpecification]] and [[QSSModelPropertyCheckSpecification]], i.e.,
 * the plan can be successfully executed with two actions.
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestSimplePropertyCheck extends FunSpec with ShouldMatchers {

  ApplicationLogger.setLogLevel(Level.SEVERE)

  describe("Simple Property Check Scenario") {

    it("works in principle :)") {

      val result = submit {
        SingleModel("java://examples.sr.LinearChainSystem")
      } {
        WallClockTimeMaximum(seconds = 30)
      } {
        exists >> model | hasProperty("qss")
      }

      result.trace.size should be >= 2
      result.trace.size should be <= 3
      result should not be ofType[FailurePlanExecutionResult]
    }

    it("returns result of a failed attempt whenever plan goals cannot be reached") {
      val result =
        submit {
          SingleModel("java://examples.sr.LinearChainSystem")
        } {
          WallClockTimeMaximum(seconds = 30)
        } {
          exists >> model | hasProperty("undefined")
        }
      //TODO: check user preferences regarding maximum tries
      result should be(ofType[FailurePlanExecutionResult])
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

    it("fails whenever multiple start-with-action-selector preferences are defined") {
      evaluating {
        submit {
          SingleModel("java://examples.sr.LinearChainSystem")
        }(
          WallClockTimeMaximum(seconds = 30), StartWithActionSelector(FirstActionSelector),
          StartWithActionSelector(DeterministicFirstActionSelector)) {
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