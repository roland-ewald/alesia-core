package alesia.planning

/**
 * A trivial planning problem for which a strong-cyclic solution exists.
 *
 * @author Roland Ewald
 *
 */
class TrivialStrongCyclicPlanningProblem(val solutionLength: Int = 3) extends PlanningProblem {

  val solvable = v("solvable")
  val doneVariables = for (i <- 1 to solutionLength) yield v("action_" + i + "_done")
  val stepVariables = for (i <- 0 to solutionLength) yield v("step_" + i)

  //  val initActionDone = v("initActionDone")
  //  val step1 = v("step1")
  //  val step1ActionDone = v("step1ActionDone")
  //  val step2 = v("step2")
  //  val step2ActionDone = v("step2ActionDone")
  //  val solved = v("solved")
  //
  //  action("init-action",
  //    solvable,
  //    Effect(initActionDone),
  //    Effect(step1, nondeterministic = true))
  //
  //  action("step1Action",
  //    step1,
  //    Effect(step1ActionDone),
  //    Effect(step2, nondeterministic = true))
  //
  //  action("step2Action",
  //    step2,
  //    Effect(step2ActionDone),
  //    Effect(solved, nondeterministic = true))

  for (i <- 1 to solutionLength)
    action("action_" + i,
      stepVariables(i - 1),
      Effect(add = List(doneVariables(i - 1))),
      Effect(add = List(stepVariables(i)), nondeterministic = true))

  val initialState = stepVariables(0)
  val goalState = doneVariables.last

}