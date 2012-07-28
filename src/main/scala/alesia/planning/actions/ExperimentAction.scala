package alesia.planning.actions

import alesia.bindings.ExperimentProvider

/** Trait for actions that involve the execution of experiments.
 *  @author Roland Ewald
 */
trait ExperimentAction extends Action[ExperimentProvider] {

  val results = scala.collection.mutable.Map[String,AnyRef]()
  
  /** The estimated cost of executing this action. */
  def estimatedCost: Double = 0.
  
  override def preconditions = Map[String, Class[_]]()

  override def constraints(p: List[AnyRef]) = true
  
  def postconditions = Map[String, Class[_]]()
  
  def potentialPercepts = Map[String, Class[_]]()
  
  def resultFor(key:String) = results.get(key)
  
  protected[this] def addResult(key:String, result:AnyRef):Unit = {
    results(key) = result
  }
}