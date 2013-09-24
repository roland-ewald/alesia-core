package alesia

import org.scalatest.FunSuite
import alesia.bindings.james.JamesExperimentProvider
import examples.sr.LinearChainSystem
import sessl.util.Logging
import alesia.planning.domain.Algorithm
import alesia.planning.domain.ParameterizedModel
import alesia.planning.domain.Model

/** Super class for all tests involved in experimentation.
 *  @author Roland Ewald
 */
abstract class ExperimentationTest extends FunSuite with Logging {

  /** The test problem. */
  val problem = ParameterizedModel(new Model("java://" + classOf[LinearChainSystem].getName))
  
  val nrm = Algorithm(sessl.james.NextReactionMethod())
}