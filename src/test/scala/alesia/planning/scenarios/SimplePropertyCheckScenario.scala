package alesia.planning.scenarios

import alesia.planning.execution.MaxOverallNumberOfActions

/**
 * Simple scenario to check a model property.
 *
 * @author Roland Ewald
 */
object SimplePropertyCheckScenario {

  import alesia.query._

  val domain = SingleModel("java://examples.sr.LinearChainSystem")

  val preferences = Seq(WallClockTimeMaximum(seconds = 30), TerminateWhen(MaxOverallNumberOfActions(10)))

  val hypothesis = exists >> model | hasProperty("qss")

}