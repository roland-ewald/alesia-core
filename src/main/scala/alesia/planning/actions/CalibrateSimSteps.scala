package alesia.planning.actions

import alesia.bindings.ExperimentProvider
import alesia.planning.domain.Problem
import sessl.util.Logging
import sessl.AbstractExperiment
import sessl.AbstractPerformanceObservation
import sessl.AfterSimSteps
import scala.math.max

/**
 * Find out how much simulation steps need to be executed before a suitable execution time is approximated.
 * Running too short (only few ms) means that stochastic noise is high and results may be biased.
 * Running too long means that computational resources are wasted.
 *
 * @author Roland Ewald
 */
object CalibrateSimSteps extends ExperimentAction with Logging {

  /** Returns approximate number of simulation steps until the desired execution time is reached. */
  def apply(p: Problem, execTime: Double, eps: Double = 0.1, maxSteps: Int = 10)(implicit e: ExperimentProvider): (Long, Double) = {

    def runtimeForSteps(s: Long): Double = {
      var rv = 0.
      val exp = e.configure(p)
      exp.stopCondition = AfterSimSteps(s)
      exp.withExperimentPerformance { r => rv = r.runtimes.head }
      sessl.execute(exp)
      logger.info("Executing calibration experiment on " + p + " for " + s + " steps: it took " + rv + " seconds.")
      rv
    }

    var steps: Long = 1
    var runtime = runtimeForSteps(steps)
    var counter = 1

    while (counter < maxSteps && (runtime <= (1 - eps) * execTime || runtime >= (1 + eps) * execTime)) {
      runtime =  runtimeForSteps(steps)
      steps *= max(10, scala.math.round(execTime / runtime ))
      counter += 1
    }
    (steps, runtime)
  }

}