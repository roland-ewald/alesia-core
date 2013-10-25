package alesia.planning.scenarios

import alesia.planning.execution.MaxOverallNumberOfActions
import alesia.query.MaxAllowedQSSError
import alesia.query.MaxSingleExecutionWallClockTime
import alesia.query.Scenario
import alesia.query.SingleModel
import alesia.query.SingleSimulator
import alesia.query.TerminateWhen
import alesia.query.exists
import alesia.query.hasProperty
import alesia.query.model
import sessl.james.NextReactionMethod

/**
 * Simple scenario to check a model property.
 *
 * @author Roland Ewald
 */
object SimplePropertyCheckScenario extends Scenario {

  val maxNumOfActions = 5

  import alesia.query._

  override val domain = Seq(
    SingleModel("java://examples.sr.LinearChainSystem"),
    SingleSimulator("nrm", NextReactionMethod()))

  override val preferences = Seq(
    TerminateWhen(MaxOverallNumberOfActions(maxNumOfActions)),
    MaxSingleExecutionWallClockTime(seconds = 2),
    //Really permissive (200% error in each direction is OK!?), because we only want to test if this works:
    MaxAllowedQSSError(4.0))

  override val hypothesis = exists >> model | hasProperty("qss")

}