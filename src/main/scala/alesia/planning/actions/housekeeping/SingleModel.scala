package alesia.planning.actions.housekeeping

import alesia.bindings.ResourceProvider
import java.io.File

/**
 * Action to introduce a single model.
 * @author Roland Ewald
 */
case class SingleModel(val url: String) extends ModelIntroduction {

  private[this] var result: Option[File] = None

  override def execute(implicit provider: ResourceProvider): Unit = {
    result = provider.getResourceAsFile(url)
  }

  override def postConditions: Map[String, Class[_]] = Map()

  override def potentialPercepts: Map[String, Class[_]] = Map()

  override def resultFor(key: String): Option[AnyRef] = None

}