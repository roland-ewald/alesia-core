package alesia.planning

/**
 * Operations to be supported by all actions defined in a planning domain.
 */
trait PlanningDomainAction {

  /**
   * Returns the set of states from which the current state can always be reached by this action.
   * @param currentState the instruction id of the current set of states
   * @return the instruction id of the set of states from which it is guaranteed that this set can be reached
   */
  def strongPreImage(currentState: Int): Int

  /**
   * Returns the set of states from which the current state may be reached by this action.
   * @param currentState the instruction id of the current set of states
   * @return the instruction id of the set of states from which it is reachable
   */
  def weakPreImage(currentState: Int): Int

}