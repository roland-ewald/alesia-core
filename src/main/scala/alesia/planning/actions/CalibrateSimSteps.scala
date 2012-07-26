package alesia.planning.actions

import alesia.bindings.ExperimentProvider
import alesia.planning.domain.Problem
import sessl.util.Logging
import sessl.AbstractExperiment
import sessl.AbstractPerformanceObservation
import sessl.AfterSimSteps
import scala.math.min
import scala.math.round
import alesia.bindings.Simulator

/**
 * Find out how much simulation steps need to be executed before a suitable execution time is approximated.
 * Running too short (only few ms) means that stochastic noise is high and results may be biased.
 * Running too long means that computational resources are wasted.
 *
 * @author Roland Ewald
 */
object CalibrateSimSteps extends ExperimentAction with Logging {

  /**
   * Apply the action.
   *
   * @param p
   *          the problem
   * @param a
   *          the algorithm
   * @param execTime
   *          the desired execution time
   * @param eps
   *          the acceptable relative deviation from the desired execution time, e.g. epsilon = 0.1 means +/- 10% deviation is OK
   * @param maxIt
   *          the maximal number of iterations
   * @param maxFactor
   *          the maximal factor by which the number of steps will be increased
   * @param provider
   *          the experiment provider
   * @return the scala. tuple2
   */
  def apply(p: Problem, a: Simulator, execTime: Double, eps: Double = 0.1, maxIt: Int = 20, maxFactor: Double = 10)(implicit provider: ExperimentProvider): (Long, Double) = {
    require(execTime > 0, "Desired execution time has to be positive.")
    require(eps > 0 && eps < 1, "Epsilon should be in (0,1).")
    require(maxIt > 1, "The maximal number of iterations should be > 1.")
    require(maxFactor > 1, "The maximal multiplication factor should be > 1.")

    def runtimeForSteps(s: Long): Double = {
      var rv = 0.
      val exp = provider.performanceExperiment(p, a)
      exp.stopCondition = AfterSimSteps(s)
      exp.withExperimentPerformance { r => rv = r.runtimes.head }
      sessl.execute(exp)
      logger.info("Executing calibration experiment on " + p + " for " + s + " steps: it took " + rv + " seconds.")
      rv
    }

    var steps: Long = 1
    var runtime = runtimeForSteps(steps)
    var counter = 1

    while (counter < maxIt && (runtime <= (1 - eps) * execTime || runtime >= (1 + eps) * execTime)) {
      runtime = runtimeForSteps(steps)
      steps = round(steps * min(maxFactor, execTime / runtime))
      logger.info("#Steps:" + steps)
      counter += 1
    }
    (steps, runtime)
  }

}