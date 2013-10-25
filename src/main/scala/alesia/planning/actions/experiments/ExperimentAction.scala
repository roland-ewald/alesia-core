package alesia.planning.actions.experiments

import alesia.bindings.ExperimentProvider
import alesia.planning.actions.Action
import alesia.planning.context.ExecutionContext

/**
 * Trait for actions that involve the execution of experiments.
 * 
 * //TODO: necessary?
 * 
 *  @author Roland Ewald
 */
trait ExperimentAction extends Action {

  /** The estimated cost of executing this action. */
  def estimatedCost: Double = .0


  
  protected[this] def addResult(key: String, result: AnyRef): Unit = {
  }
}