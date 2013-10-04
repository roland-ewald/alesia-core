package alesia.planning.scenarios

import alesia.planning.execution.MaxOverallNumberOfActions
import alesia.planning.execution.WallClockTimeMaximum

/**
 * Simple scenario to check a model property.
 *
 * @author Roland Ewald
 */
object SimplePropertyCheckScenario {

  val maxNumOfActions = 5

  import alesia.query._

  val domain = SingleModel("java://examples.sr.LinearChainSystem")

  val preferences = Seq(TerminateWhen(MaxOverallNumberOfActions(maxNumOfActions)))

  val hypothesis = exists >> model | hasProperty("qss")

}