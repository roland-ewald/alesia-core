package alesia.planning.execution

/**
 * Decides which of the (potentially multiple) actions of a [[Plan]] a [[PlanExecutor]] should select.
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
  override def apply(actionIndices: Iterable[Int], state: ExecutionState) = {
    val randIndex = math.round(scala.math.random * actionIndices.size).toInt
    val elems = actionIndices.toArray
    (elems(randIndex), this)
  }
}

//TODO: implement 'smart' action selector:

case class PatientActionSelector(val degreeOfPatience: Int) extends ActionSelector {
  override def apply(actionIndices: Iterable[Int], state: ExecutionState) = ???
}