package alesia.planning.planners.nondet

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import alesia.utils.bdd.UniqueTable
import alesia.planning.domain.Domain

/**
 * Tests for the non-deterministic policy planner.
 *
 * @author Roland Ewald
 *
 */
@RunWith(classOf[JUnitRunner])
class NonDeterministicPolicyPlannerTest extends FunSpec {

  describe("The OBDD-Planner") {

    it("is able to solve sample problem given in 'Automatic OBDD-based Generation of Universal Plans in Non-Deterministic Domains', by Cimatti et al. '98") {

      val domain = new Domain {

        val posTrainStation = v("pos=train-station")
        val posVictoriaStation = v("pos=Victoria-station")
        val posGatwick = v("pos=Gatwick")
        val posCityCenter = v("pos=city-center")
        val posTruckStation = v("pos=truck-station")
        val posAirStation = v("pos=air-station")
        val posLuton = v("pos=Luton")
        //TODO: Simple way to manage exclusive boolean variables? 
        //(to prevent being at train station and Victoria station at the same time, for example)

        val lightIsGreen = v("light=green")
        val fuel = v("fuel")
        val trafficJam = v("fuel")
        val fog = v("fog")

        //Action drive-train
        val driveTrain_precond = posTrainStation or (posVictoriaStation and lightIsGreen)
        val driveTrain_effect = Effect(posTrainStation, posVictoriaStation)
        val driveTrain_effect2 = Effect(posVictoriaStation and lightIsGreen, add = List(posGatwick), del = List(posVictoriaStation))

        //Action wait-at-light 
        val waitAtLight_precond = posVictoriaStation

        //Action drive-truck
        val driveTruck_precond = (posTruckStation and fuel) or (posCityCenter and fuel and !trafficJam)
        val driveTruck_effect = Effect(posTruckStation, posCityCenter)
        val driveTruck_effect2 = Effect(posCityCenter, posGatwick)
        val driveTruck_effect3 = Effect(fuel, del = List(fuel), nondeterministic = true)

        //Action drive-truck-back
        val driveTruckBack_precond = posCityCenter and fuel and trafficJam
        val driveTruckBack_effect = Effect(posCityCenter, posTruckStation)
        val driveTruckBack_effect1 = Effect(fuel, del = List(fuel), nondeterministic = true)

        //Action make-fuel
        val makeFuel_precond = !fuel and (posCityCenter or posTruckStation)
        val makeFuel_effect = fuel

        //Action fly
        val fly_precond = posAirStation or posLuton
        val fly_effect1 = Effect(!fog and posAirStation, add = List(posGatwick), del = List(posAirStation))
        val fly_effect2 = Effect(fog and posAirStation, add = List(posLuton), del = List(posAirStation))
        val fly_effect3 = Effect(posLuton, posAirStation)

        //Action air-truck-transit
        val airTruckTransit_precond = posAirStation
        val airTruckTransit_effect = Effect(posAirStation, posTruckStation)

        //Initial state
        val initialState = posTrainStation or posAirStation or (posTruckStation and fuel)

        //Goal
        val goal = posGatwick
      }

      println("vars:" + domain.numVariables)
      println("functions:" + domain.numFunctions)

      pending
    }

    it("is able to solve a simple problem in the ALeSiA domain") {
      pending
    }

    it("terminates when confronted with a planning problem that does not have a solution") {
      pending
    }

    it("warns the user when the planning problem is strangely defined (no actions etc.)") {
      pending
    }

    it("warns the user and stops execution when the state space grows too large") {
      pending
    }
  }

}