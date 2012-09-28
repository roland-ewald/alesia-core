package alesia

import org.scalatest.FunSuite

import alesia.bindings.james.JamesExperimentProvider
import alesia.planning.domain.ProblemSpaceElement
import examples.sr.LinearChainSystem
import sessl.util.Logging

/** Super class for all tests involved in experimentation.
 *  @author Roland Ewald
 */
abstract class ExperimentationTest extends FunSuite with Logging {

  /** The experiment provider. */
  implicit val expProvider = JamesExperimentProvider

  /** The test problem. */
  val problem = ProblemSpaceElement("java://" + classOf[LinearChainSystem].getName)
}