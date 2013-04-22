package alesia.planning.actions

/** General interface for actions.
 *  @param [A] the context provider required to execute the action
 *  @author Roland Ewald
 */
trait Action[-A] {

  def preconditions: Map[String, Class[_]] = Map()
  
  def constraints(precons: List[AnyRef]): Boolean = true

  def execute(implicit provider: A): Unit
  
  def postConditions: Map[String, Class[_]] = Map()
  
  def potentialPercepts: Map[String, Class[_]] = Map()

  def resultFor(key:String):Option[AnyRef]= None
}