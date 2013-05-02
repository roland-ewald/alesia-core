package alesia.planning.actions

/**
 * General interface for actions. An action is completely pre-configured and ready to be executed.
 *
 *  @param [A] the context provider required to execute the action
 *  @author Roland Ewald
 */
trait Action[-A] {

  /** Execute the action. */
  def execute(implicit provider: A): Unit

  /** Get the results associated with all literals. */
  def resultFor(key: String): Option[AnyRef] = None

}