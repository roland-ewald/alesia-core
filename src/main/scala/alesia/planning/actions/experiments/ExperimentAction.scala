package alesia.planning.actions.experiments

import alesia.bindings.ExperimentProvider
import alesia.planning.actions.Action

/**
 * Trait for actions that involve the execution of experiments.
 *  @author Roland Ewald
 */
trait ExperimentAction extends Action[ExperimentProvider] {

  val results = scala.collection.mutable.Map[String, AnyRef]()

  /** The estimated cost of executing this action. */
  def estimatedCost: Double = .0

  override def resultFor(key: String) = results.get(key)

  protected[this] def addResult(key: String, result: AnyRef): Unit = {
    results(key) = result
  }
}