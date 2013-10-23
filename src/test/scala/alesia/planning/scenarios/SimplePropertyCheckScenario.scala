package alesia.planning.scenarios

import alesia.planning.execution.MaxOverallNumberOfActions
import alesia.planning.execution.WallClockTimeMaximum
import alesia.query.Scenario

/**
 * Simple scenario to check a model property.
 *
 * @author Roland Ewald
 */
object SimplePropertyCheckScenario extends Scenario {

  val maxNumOfActions = 5

  import alesia.query._

  override val domain = Seq(SingleModel("java://examples.sr.LinearChainSystem"))

  override val preferences = Seq(TerminateWhen(MaxOverallNumberOfActions(maxNumOfActions)))

  override val hypothesis = exists >> model | hasProperty("qss")

}