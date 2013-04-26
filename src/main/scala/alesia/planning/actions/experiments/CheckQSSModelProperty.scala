package alesia.planning.actions.experiments

import scala.language.existentials

import sessl.util.Logging
import alesia.bindings.ExperimentProvider
import alesia.bindings.Simulator
import sessl.AfterWallClockTime
import sessl.AfterSimSteps
import alesia.planning.domain.ParameterizedModel

/**
 * Checks whether this model exhibits a quasi-steady state property and, if so, from which point in simulation time on.
 *
 * @author Roland Ewald
 */
case class CheckQSSModelProperty(problem: ParameterizedModel, sim: Simulator,
  maxExecTimeSeconds: Double, linearSteps: Int = 3, errorAllowed: Double = 0.1) extends ExperimentAction with Logging {

  override def execute(implicit pr: ExperimentProvider) {
    val pessimisticStepRuntime = pr.executeForNSteps(problem, sim, 1)
    val pessimisticMaxSteps = math.round(maxExecTimeSeconds / pessimisticStepRuntime)
    println(pessimisticMaxSteps)
    require(pessimisticMaxSteps > 10, "Maximal execution time is too small, same magnitude as the minimal time (executing a single step).")

    var runtime = pr.observeRuntimeFor(problem, sim) { exp =>
      val ms = math.round((maxExecTimeSeconds - maxExecTimeSeconds.toInt) * 1000L)
      exp.stopCondition = AfterWallClockTime(seconds = maxExecTimeSeconds.toInt, milliseconds = ms.toInt)
    }

    //TODO: check if last three--five points form a line???

  }

}