package alesia.planning.actions

import scala.reflect._

import org.reflections.Reflections

/**
 * @author Roland Ewald
 */
object ActionRegistry extends App {

  val reflections = new Reflections("alesia.planning.actions")

  val subTypes = reflections.getSubTypesOf(classOf[ActionSpecification[_, _]])

  val it = subTypes.iterator()

  while (it.hasNext())
    println("Detected action: " + it.next + "\n")

}