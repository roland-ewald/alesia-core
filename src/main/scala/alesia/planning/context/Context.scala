package alesia.planning.context

/**
 * The current context of a system. On this basis, a plan decides upon the next action(s).
 *
 * @author Roland Ewald
 *
 */
trait Context {

}

/** Empty context */
case object EmptyContext extends Context