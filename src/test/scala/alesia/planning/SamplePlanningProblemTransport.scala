package alesia.planning

/**
 * This is a representation of the planning problem used as an example in:
 *
 *  A. Cimatti, M. Roveri, and P. Traverso, "Automatic OBDD-based generation of universal plans in Non-Deterministic domains," Tech. Rep. 9801-10, Jan. 1998.
 *
 * @author Roland Ewald
 */
class SamplePlanningProblemTransport extends PlanningProblem {

  //Variables
  val lightIsGreen = v("light=green")
  val fuel = v("fuel")
  val trafficJam = v("fuel")
  val fog = v("fog")

  //TODO: Simple way to manage exclusive boolean variables(to prevent being at train station and Victoria station at the same time, for example)? 
  //Maybe a new function v(name, possibilities...) which returns a list of variables?
  val posTrainStation = v("pos=train-station")
  val posVictoriaStation = v("pos=Victoria-station")
  val posGatwick = v("pos=Gatwick")
  val posCityCenter = v("pos=city-center")
  val posTruckStation = v("pos=truck-station")
  val posAirStation = v("pos=air-station")
  val posLuton = v("pos=Luton")

  //Initial and goal state
  val initialState = (posTrainStation or posAirStation or (posTruckStation and fuel))
  val goalState = posGatwick

  //Actions
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

  action("air-truck-transit",
    posAirStation,
    Effect(posAirStation, posTruckStation))
}