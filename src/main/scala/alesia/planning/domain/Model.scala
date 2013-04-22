package alesia.planning.domain

import java.net.URI

/**
 * Represents a model.
 *
 * @author Roland Ewald
 */
case class Model(val id: String) {
  def asURI = new URI(id)
}

/**
 * Represents a parameterized model.
 */
case class ParameterizedModel(val model: Model, val parameters: Map[String, Any] = Map())