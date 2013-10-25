package alesia.planning.actions

/**
 * Contains all publicly shared literals.
 *
 * @author Roland Ewald
 */
object SharedLiterals {

  /** A model that has been 'loaded' in some sense. Linked to [[alesia.planning.domain.ParameterizedModel]].*/
  val loadedModel = entity("loaded", "Model")

  /** A model that has been calibrated by all single simulators. */
  val calibratedModel = entity("calibrated", loadedModel)

  /** Defines format of properties. */
  def property(name: String, literal: String) = s"${name}(${literal})"

  /** Defines format of describing entities. */
  def entity(adjective: String, literal: String) = adjective + literal.headOption.map(_.toUpper) + literal.tail
}