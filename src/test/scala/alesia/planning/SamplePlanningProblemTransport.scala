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
  val trafficJam = v("trafficJam")
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

  //  val positions = Array[Int](posTrainStation, posVictoriaStation, posGatwick, posCityCenter, posTruckStation, posAirStation, posLuton)
  //
  //  def noneExceptOne(fs: IndexedSeq[Int], positive: Int): Int = fs.zipWithIndex.foldLeft(1)((acc, f) => {
  //    if (f._2 == positive)
  //      table.and(acc, f._1)
  //    else table.and(acc, table.not(f._1))
  //  })
  //
  //  val invariant = (0 until positions.length).toList.map(noneExceptOne(positions, _)).foldLeft(0)(table.or)

  //Initial and goal state
  val initialState = (posTrainStation or posAirStation or (posTruckStation and fuel))
  val goalState = posGatwick

  //Actions
  val driveTrain = action("drive-train",
    posTrainStation or (posVictoriaStation and lightIsGreen),
    Effect(posTrainStation, add = List(posVictoriaStation), del = List(posTrainStation, posGatwick)),
    Effect(posVictoriaStation and lightIsGreen, add = List(posGatwick), del = List(posVictoriaStation, posTrainStation)))

  val waitAtLight = action("wait-at-light", posVictoriaStation)

  val driveTruck = action("drive-truck",
    (posTruckStation and fuel) or (posCityCenter and fuel and !trafficJam),
    Effect(posTruckStation, add = List(posCityCenter), del = List(posTruckStation, posGatwick)),
    Effect(posCityCenter, add = List(posGatwick), del = List(posCityCenter, posTruckStation)),
    Effect(fuel, del = List(fuel), nondeterministic = true))

  val driveTruckBack = action("drive-truck-back",
    posCityCenter and fuel and trafficJam,
    Effect(posCityCenter, posTruckStation),
    Effect(fuel, del = List(fuel), nondeterministic = true))

  val makeFuel = action("make-fuel",
    (!fuel) and (posCityCenter or posTruckStation),
    Effect(TrueVariable, add = List(fuel)))

  val fly = action("fly",
    posAirStation or posLuton, //Careful with encoding...:
    Effect((!fog) and posAirStation, add = List(posGatwick), del = List(posAirStation, posLuton)),
    Effect(fog and posAirStation, add = List(posLuton), del = List(posAirStation, posGatwick)),
    Effect(posLuton, add = List(posAirStation), del = List(posLuton, posGatwick)))

  val airTruckTransit = action("air-truck-transit",
    posAirStation,
    Effect(posAirStation, posTruckStation))
}