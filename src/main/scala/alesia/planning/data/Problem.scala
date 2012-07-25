package alesia.planning.data

/**
 * An element from the problem space.
 *
 * @author Roland Ewald
 */
case class Problem(val model: String, val parameters: Map[String, Any])