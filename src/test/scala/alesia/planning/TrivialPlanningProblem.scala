package alesia.planning

/**
 * Trivial planning problems for testing.
 *
 * @author Roland Ewald
 *
 */
class TrivialPlanningProblem extends PlanningProblem {

  val solvable = v("solvable")
  val solved = v("solved")

  val initialState = solvable
  val goalState = solved

}

class TrivialPlanningProblemSolvableDeterministic extends TrivialPlanningProblem {
  val solve = action("solve", solvable, Effect(solvable, add = List(solved)))
}

class TrivialPlanningProblemSolvableNonDeterministic extends TrivialPlanningProblem {
  val useActA = v("use-action-a")
  val useActB = v("use-action-b")
  val solveWithA = action("solveWithA", solvable and useActA, Effect(solvable and useActA, add = List(solved)))
  val solveWithB = action("solveWithB", solvable and useActB, Effect(solvable and useActB, add = List(solved)))
  val trySolutions = action("trySolutions", solvable, Effect(solvable, add = List(useActA or useActB)))
}