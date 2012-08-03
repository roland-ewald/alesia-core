package alesia

import org.junit.Test
import alesia.bindings.james.JamesExperimentProvider
import sessl.util.Logging
import alesia.planning.domain.Problem
import examples.sr.LinearChainSystem

/** Super class for all tests involved in experimentation.
 *  @author Roland Ewald
 */
@Test
class ExperimentationTest extends Logging {

  /** The experiment provider. */
  implicit val expProvider = JamesExperimentProvider

  /** The test problem. */
  val problem = Problem("java://" + classOf[LinearChainSystem].getName)
}