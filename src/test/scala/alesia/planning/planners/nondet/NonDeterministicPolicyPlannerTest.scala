package alesia.planning.planners.nondet

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner

import alesia.planning.PlanningDomain

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

      val domain = new PlanningDomain {

        val posTrainStation = v("pos=train-station")
        val posVictoriaStation = v("pos=Victoria-station")
        val posGatwick = v("pos=Gatwick")
        val posCityCenter = v("pos=city-center")
        val posTruckStation = v("pos=truck-station")
        val posAirStation = v("pos=air-station")
        val posLuton = v("pos=Luton")
        //TODO: Simple way to manage exclusive boolean variables(to prevent being at train station and Victoria station at the same time, for example)? 
        //Maybe a new function v(name, possibilities...) which returns a list of variables? 

        val lightIsGreen = v("light=green")
        val fuel = v("fuel")
        val trafficJam = v("fuel")
        val fog = v("fog")

        action("drive-train",
          posTrainStation or (posVictoriaStation and lightIsGreen),
          Effect(posTrainStation, posVictoriaStation),
          Effect(posVictoriaStation and lightIsGreen, add = List(posGatwick), del = List(posVictoriaStation)))

        action("wait-at-light", posVictoriaStation)

        action("drive-truck",
          (posTruckStation and fuel) or (posCityCenter and fuel and !trafficJam),
          Effect(posTruckStation, posCityCenter),
          Effect(posCityCenter, posGatwick),
          Effect(fuel, del = List(fuel), nondeterministic = true))

        action("drive-truck-back",
          posCityCenter and fuel and trafficJam,
          Effect(posCityCenter, posTruckStation),
          Effect(fuel, del = List(fuel), nondeterministic = true))

        action("make-fuel",
          !fuel and (posCityCenter or posTruckStation),
          Effect(TrueVariable, add = List(fuel)))

        action("fly",
          posAirStation or posLuton,
          Effect(!fog and posAirStation, add = List(posGatwick), del = List(posAirStation)),
          Effect(fog and posAirStation, add = List(posLuton), del = List(posAirStation)),
          Effect(posLuton, posAirStation))

        //Action air-truck-transit
        action("air-truck-transit",
          posAirStation,
          Effect(posAirStation, posTruckStation))

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