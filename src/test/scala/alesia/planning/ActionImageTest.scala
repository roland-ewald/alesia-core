package alesia.planning

import sessl.util.Logging
import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Test image opertions of actions.
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class ActionImageTest extends FunSpec with Logging {

  val detProblem = new TrivialPlanningProblemSolvableDeterministic

  describe("The weak pre-image of an action") {

    it("is computed correctly for a trivial planning problem") {
      assert(detProblem.solve.weakPreImage(detProblem.goalStates) === detProblem.solvable.id)
    }

  }

  describe("The strong pre-image of an action") {

    it("is computed correctly for a trivial planning problem") {
      assert(detProblem.solve.strongPreImage(detProblem.goalStates) === detProblem.solvable.id)
    }
  }

}