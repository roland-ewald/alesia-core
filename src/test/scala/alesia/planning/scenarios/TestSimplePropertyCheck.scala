package alesia.planning.scenarios

import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import alesia.results.FailurePlanExecutionResult
import org.junit.Assert._
import org.jamesii.core.util.logging.ApplicationLogger
import java.util.logging.Level
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.matchers.BePropertyMatchResult
import org.scalatest.matchers.BePropertyMatcher
import alesia.TestUtils._
import alesia.query._
import alesia.planning.execution.FirstActionSelector
import alesia.planning.execution.MinActionIndexSelector
import alesia.planning.execution.MaxOverallNumberOfActions
import alesia.planning.execution.WallClockTimeMaximum
import alesia.planning.execution.RandomActionSelector

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

  import SimplePropertyCheckScenario._

  describe("Simple Property Check Scenario") {

    it("works in principle :)") {

      val result = submit(SimplePropertyCheckScenario)

      result.numOfActions should be(2)
      result should not be ofType[FailurePlanExecutionResult]
    }

    it("returns result of a failed attempt whenever plan goals cannot be reached") {
      val result =
        submit {
          domain: _*
        } {
          preferences: _*
        } {
          exists >> model | hasProperty("undefined")
        }
      result should be(ofType[FailurePlanExecutionResult])
      result.numOfActions should be(maxNumOfActions)
    }

    it("returns result of a failed attempt whenever there is no time to execute the plan") {
      val result =
        submit {
          domain: _*
        } {
          TerminateWhen(WallClockTimeMaximum(milliseconds = 1))
        } {
          hypothesis
        }
      result should be(ofType[FailurePlanExecutionResult])
      result.numOfActions should be(1)
    }

    it("returns result of a failed attempt whenever no termination condition is defined") {
      val result =
        submit {
          domain: _*
        } {
          StartWithActionSelector(RandomActionSelector)
        } {
          hypothesis
        }
      result should be(ofType[FailurePlanExecutionResult])
    }

    it("fails whenever elements of the problem specification are missing, and thus planning fails") {
      evaluating {
        submit {
          new UserDomainEntity {}
        } {
          preferences: _*
        } {
          hypothesis
        }
      } should produce[Exception]
    }

    it("fails whenever multiple start-with-action-selector preferences are defined") {
      evaluating {
        submit {
          domain: _*
        }(
          TerminateWhen(WallClockTimeMaximum(seconds = 30)), StartWithActionSelector(FirstActionSelector),
          StartWithActionSelector(MinActionIndexSelector)) {
            hypothesis
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