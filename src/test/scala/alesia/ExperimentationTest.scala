package alesia

import alesia.bindings.james.JamesExperimentProvider
import sessl.util.Logging
import alesia.planning.domain.Problem
import examples.sr.LinearChainSystem
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/** Super class for all tests involved in experimentation.
 *  @author Roland Ewald
 */
abstract class ExperimentationTest extends FunSuite with Logging {

  /** The experiment provider. */
  implicit val expProvider = JamesExperimentProvider

  /** The test problem. */
  val problem = Problem("java://" + classOf[LinearChainSystem].getName)
}