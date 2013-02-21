package alesia.planning

/**
 * A trivial planning problem for which a strong-cyclic solution exists.
 *
 * @author Roland Ewald
 *
 */
class TrivialStrongCyclicPlanningProblem(val solutionLength: Int = 3) extends PlanningProblem {

  val doneVariables = for (i <- 1 to solutionLength) yield v("action_" + i + "_done")
  val stepVariables = for (i <- 0 to solutionLength) yield v("step_" + i)

  for (i <- 1 to solutionLength)
    action("action_" + i,
      stepVariables(i - 1),
      Effect(add = List(doneVariables(i - 1))),
      Effect(add = List(stepVariables(i)), nondeterministic = true))

  val initialState = stepVariables(0)
  val goalState = doneVariables.last

}