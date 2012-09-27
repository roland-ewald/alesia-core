package alesia

import org.junit.Test
import alesia.bindings.james.JamesExperimentProvider
import sessl.util.Logging
import examples.sr.LinearChainSystem
import alesia.planning.domain.ProblemSpaceElement

/** Super class for all tests involved in experimentation.
 *  @author Roland Ewald
 */
@Test
class ExperimentationTest extends Logging {

  /** The experiment provider. */
  implicit val expProvider = JamesExperimentProvider

  /** The test problem. */
  val problem = ProblemSpaceElement("java://" + classOf[LinearChainSystem].getName)
}