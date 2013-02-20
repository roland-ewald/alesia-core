package alesia.planning

/**
 * Operations to be supported by all actions defined in a planning domain. 
 */
trait PlanningDomainAction {

  /**
   * The strong pre-image of this action.
   */
  def strongPreImage(currentState: Int): Int

  /**
   * The weak pre-image of this action.
   */
  def weakPreImage(currentState: Int): Int

}