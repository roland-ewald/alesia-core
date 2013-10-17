package alesia.planning.execution

/**
 * Decides which of the (potentially multiple) actions of a [[alesia.planning.plans.Plan]] a
 * [[alesia.planning.execution.PlanExecutor]] should select.
 *
 * @author Roland Ewald
 */
trait ActionSelector {

  /**
   * Select one of the given actions.
   *
   * @param actionIndices the action indices
   * @param state the execution state
   * @return tuple with selected index and action selector to be used next
   */
  def apply(actionIndices: Iterable[Int], state: ExecutionState): (Int, ActionSelector)

}

/** Always select the first action in the list. */
object FirstActionSelector extends ActionSelector {
  override def apply(actionIndices: Iterable[Int], state: ExecutionState) = (actionIndices.head, this)
}

/** Always select the action with the lowest index in the list. */
object DeterministicFirstActionSelector extends ActionSelector {
  override def apply(actionIndices: Iterable[Int], state: ExecutionState) = (actionIndices.min, this)
}

/** Always pick a random action. */
object RandomActionSelector extends ActionSelector {

  override def apply(actionIndices: Iterable[Int], state: ExecutionState) = (pick(actionIndices), this)

  def pick(actionIndices: Iterable[Int]): Int =
    (actionIndices.toVector)(math.round(scala.math.random * actionIndices.size).toInt)
}

/**
 * Pick deterministically until an action has been selected for a certain number of times,
 * then pick randomly another action.
 *
 * @param degreeOfPatience the number of times the same action may be selected before falling back to random choice
 * @param lastActionIndex index of the last action that has been chosen
 * @param trials number of trials the last action has been chosen already
 */
case class PatientActionSelector(val degreeOfPatience: Int, val lastActionIndex: Int = -1, val trials: Int = 0) extends ActionSelector {
  override def apply(actionIndices: Iterable[Int], state: ExecutionState) = {

    val patient = trials < degreeOfPatience

    require(patient || actionIndices.size > 1 || actionIndices.head != lastActionIndex,
      s"Action #${lastActionIndex} was already chosen ${trials} times, but there is no alternative")

    val newIndex =
      if (!patient)
        RandomActionSelector.pick(actionIndices.toSet - lastActionIndex)
      else
        actionIndices.min

    val newTrials =
      if (newIndex == lastActionIndex)
        trials + 1
      else
        1

    (newIndex, PatientActionSelector(degreeOfPatience, newIndex, newTrials))
  }
}