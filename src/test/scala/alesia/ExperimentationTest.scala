package alesia

import org.scalatest.FunSuite
import alesia.bindings.james.JamesExperimentProvider
import examples.sr.LinearChainSystem
import sessl.util.Logging
import alesia.planning.domain.Algorithm
import alesia.planning.domain.ParameterizedModel
import alesia.query.SingleSimulator
import org.scalatest.FunSpec

/**
 * Super class for all tests involved in experimentation.
 *  @author Roland Ewald
 */
abstract class ExperimentationTest extends FunSpec with Logging {

  /** The test problem. */
  val problem = ParameterizedModel("java://" + classOf[LinearChainSystem].getName)

  val nrm = SingleSimulator("nrm", sessl.james.NextReactionMethod())
}