package alesia.planning.actions

/** Trait for actions that involve the execution of experiments.
 *  @author Roland Ewald
 */
trait ExperimentAction extends Action {

  /** The estimated cost of executing this action. */
  def estimatedCost: Double = 0.
}