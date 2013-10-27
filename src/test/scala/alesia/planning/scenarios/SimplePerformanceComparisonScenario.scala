package alesia.planning.scenarios

import alesia.planning.execution.MaxOverallNumberOfActions
import alesia.query.Scenario
import alesia.planning.domain.ParameterizedAlgorithm
import sessl.james.NextReactionMethod
import sessl.james.TauLeaping

/**
 * Simple scenario to compare the performance of two simulators.
 *
 * @author Roland Ewald
 */
object SimplePerformanceComparisonScenario extends Scenario {

  import alesia.query._
  
  val maxActionNumber = 4

  override val domain = Seq(
    ModelSet("java://examples.sr.LinearChainSystem",
      ModelParameter[Int]("numOfSpecies", 1, 10, 1000),
      ModelParameter[Int]("numOfInitialParticles", 10, 100, 10000)),
    SingleSimulator("nrm", NextReactionMethod()),
    SingleSimulator("tl", TauLeaping(epsilon = 0.025)))

  override val preferences = Seq(
    TerminateWhen(MaxOverallNumberOfActions(maxActionNumber)), MaxSingleExecutionWallClockTime(seconds = 1))

  override val hypothesis = exists >> model | (hasProperty("qss") and isFaster("tl", "nrm", model))

}