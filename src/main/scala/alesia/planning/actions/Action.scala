package alesia.planning.actions

/** General interface for actions.
 *  @param [A] the context provider required to execute the action
 *  @author Roland Ewald
 */
trait Action[A] {

  def preconditions: Map[String, Class[_]]
  
  def constraints(precons: List[AnyRef]): Boolean

  def execute(implicit provider: A): Unit
  
  def postconditions: Map[String, Class[_]]
  
  def potentialPercepts: Map[String, Class[_]]

  def resultFor(key:String):Option[AnyRef]
}