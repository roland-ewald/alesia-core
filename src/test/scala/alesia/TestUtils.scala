package alesia

import org.scalatest.matchers.BePropertyMatchResult
import org.scalatest.matchers.BePropertyMatcher

/**
 * Testing utilities.
 *
 * @author Roland Ewald
 */
object TestUtils {

  /**
   * Allows to check whether a result is of a certain type.
   *
   * Usage:
   *
   * {{{
   * results should be ofType[Int]
   * }}}
   *
   * Originally from Bill Venners: https://groups.google.com/d/msg/scalatest-users/UrdRM6XHB4Y/dpOzl3iSxqoJ .
   *
   * As pointed out at http://stackoverflow.com/q/8561898/109942 (http://stackoverflow.com/a/8563285/109942),
   * it is planned to be included in a later version of ScalaTest (https://groups.google.com/d/msg/scalatest-users/HeOKgs5PC2o/dDXyolfrkoMJ).
   *
   * TODO: Check ScalaTest 2.0 release when it's out (the milestones do not seem to support this)
   */
  def ofType[T](implicit m: Manifest[T]) = {
    val c = m.runtimeClass.asInstanceOf[Class[T]]
    new BePropertyMatcher[AnyRef] {
      def apply(left: AnyRef) =
        BePropertyMatchResult(c.isAssignableFrom(left.getClass), s"an instance of ${c.getName}")
    }
  }

}