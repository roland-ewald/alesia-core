package alesia.planning.scenarios

import alesia.planning.execution.MaxOverallNumberOfActions

/**
 * Simple scenario to check a model property.
 *
 * @author Roland Ewald
 */
object SimplePropertyCheckScenario {

  val maxNumOfActions = 5

  import alesia.query._

  val domain = SingleModel("java://examples.sr.LinearChainSystem")

  val preferences = Seq(WallClockTimeMaximum(seconds = 30), TerminateWhen(MaxOverallNumberOfActions(maxNumOfActions)))

  val hypothesis = exists >> model | hasProperty("qss")

}