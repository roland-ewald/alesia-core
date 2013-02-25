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

  val cyclicProblem = new TrivialStrongCyclicPlanningProblem(80)

  val transportProblem = new SamplePlanningProblemTransport

  describe("The weak pre-image of an action") {

    it("is computed correctly for a deterministic action in a trivial planning problem") {
      import detProblem._
      assert(solve.weakPreImage(goalStates) === solvable.id)
    }

    it("is computed correctly for a non-determinstic action in a trivial planning problem") {
      import nonDetProblem._
      assert(solveWithA.weakPreImage(goalStates) === table.and(solvable, useActA))
      assert(solveWithB.weakPreImage(goalStates) === table.and(solvable, useActB))
      assert(trySolutions.weakPreImage(useActA) == solvable.id)
      assert(trySolutions.weakPreImage(useActB) == solvable.id)
    }

    it("is computed correctly for a non-determinstic action in a planning problem with strong-cyclic solution") {
      import cyclicProblem._
      for (i <- 1 to solutionLength) {
        println("#vars:" + cyclicProblem.table.variableCount + ", #instr:" + cyclicProblem.numFunctions)
        assert(stepActions(i - 1).weakPreImage(stepVariables(i)) === stepVariables(i - 1).id)
      }
    }

    it("is computed correctly for the actions in the sample transport planning domain") {
      import transportProblem._
      import table._
      assert(fly.weakPreImage(posLuton) === (fog and posAirStation).id)
      assert(fly.weakPreImage(posGatwick) === (!fog and posAirStation).id)
      assert(fly.weakPreImage(posAirStation) === posLuton.id)
      assert(makeFuel.weakPreImage((posAirStation and !posCityCenter) and !posTruckStation) === FalseVariable.id)
      assert(airTruckTransit.weakPreImage(posTruckStation) === posAirStation.id)
      assert(waitAtLight.weakPreImage(posVictoriaStation) === posVictoriaStation.id)
      assert(waitAtLight.weakPreImage(not(posVictoriaStation)) === FalseVariable.id)
      assert(driveTruckBack.weakPreImage(posTruckStation) === driveTruckBack.precondition.id)
      assert(driveTruck.weakPreImage(posCityCenter) === (posTruckStation and fuel).id)
      assert(isContained(posCityCenter and fuel and !trafficJam, driveTruck.weakPreImage(posGatwick)))
    }

  }

  describe("The strong pre-image of an action") {

    it("is computed correctly for a deterministic action in a trivial planning problem") {
      import detProblem._
      assert(solve.strongPreImage(goalStates) === solvable.id)
    }

    it("is computed correctly for a non-deterministic action in a trivial planning problem") {
      import nonDetProblem._
      assert(solveWithA.strongPreImage(goalStates) === table.and(solvable, useActA))
      assert(solveWithB.strongPreImage(goalStates) === table.and(solvable, useActB))
      //      debug(trySolutions.strongPreImage(useActA), "wrong strong-preimg") FIXME
      //      assert(trySolutions.strongPreImage(useActA) == FalseVariable.id)
    }

  }

}