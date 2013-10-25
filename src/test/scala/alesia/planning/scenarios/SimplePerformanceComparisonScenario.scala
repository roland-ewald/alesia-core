package alesia.planning.scenarios

import alesia.planning.execution.MaxOverallNumberOfActions
import alesia.query.Scenario

/**
 * Simple scenario to compare the performance of two simulators.
 *
 * @author Roland Ewald
 */
object SimplePerformanceComparisonScenario extends Scenario {

  import alesia.query._

  override val domain = Seq(
    ModelSet("java://examples.sr.LinearChainSystem",
      ModelParameter[Int]("numOfSpecies", 1, 10, 1000),
      ModelParameter[Int]("numOfInitialParticles", 10, 100, 10000)))

  override val preferences = Seq(TerminateWhen(MaxOverallNumberOfActions(4)))

  override val hypothesis = exists >> model | hasProperty("todo")

}