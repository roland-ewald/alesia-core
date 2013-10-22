package alesia.results

import alesia.planning.scenarios.SimplePropertyCheckScenario
import alesia.planning.execution.WallClockTimeMaximum

/**
 * Some execution results that are useful for testing different result analysis components.
 *
 * @author Roland Ewald
 */
object TestScenarios {

  import alesia.query._
  import SimplePropertyCheckScenario._

  lazy val normalResult = submit {
    domain
  } {
    preferences: _*
  } {
    hypothesis
  }

  lazy val failureResult = alesia.query.submit {
    domain
  } {
    TerminateWhen(WallClockTimeMaximum(milliseconds = 1))
  } {
    hypothesis
  }

}