package alesia.planning.domain

import java.net.URI

/**
 * An element from the problem space.
 *
 * @author Roland Ewald
 */
case class Problem(val model: String, val parameters: Map[String, Any] = Map()) {

  def modelURI = new URI(model)
}
