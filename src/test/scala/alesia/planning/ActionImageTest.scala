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

  val nonDetProblem = new TrivialPlanningProblemSolvableNonDeterministic

  describe("The weak pre-image of an action") {

    it("is computed correctly for a determinstic action in a trivial planning problem") {
      import detProblem._
      assert(solve.weakPreImage(goalStates) === solvable.id)
    }

    it("is computed correctly for a non-determinstic action in a trivial planning problem") {
      import nonDetProblem._
      assert(solveWithA.weakPreImage(goalStates) === table.and(solvable, useActA))
      assert(solveWithB.weakPreImage(goalStates) === table.and(solvable, useActB))
      assert(trySolutions.weakPreImage(useActA) == solvable.id)
    }

  }

  describe("The strong pre-image of an action") {

    it("is computed correctly for a determinstic action in a trivial planning problem") {
      import detProblem._
      assert(solve.strongPreImage(goalStates) === solvable.id)
    }

  }

}