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

  override val domain = Seq(SingleModel("java://examples.sr.LinearChainSystem"))

  override val preferences = Seq(TerminateWhen(MaxOverallNumberOfActions(4)))

  override val hypothesis = exists >> model | hasProperty("todo")

}