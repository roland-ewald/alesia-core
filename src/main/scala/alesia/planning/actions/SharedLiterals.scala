package alesia.planning.actions

/**
 * Contains all publicly shared literals.
 *
 * @author Roland Ewald
 */
object SharedLiterals {

  /** A model that has been 'loaded' in some sense. Linked to [[alesia.planning.domain.ParameterizedModel]].*/
  val loadedModel = "loadedModel"

  /** Defines format of properties. */
  def property(name: String, literal: String) = s"${name}(${literal})"
}