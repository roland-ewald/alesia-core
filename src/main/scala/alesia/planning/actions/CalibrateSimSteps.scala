package alesia.planning.actions

import sessl.AbstractExperiment
import sessl.AfterSimSteps
import sessl.AbstractPerformanceObservation
import sessl.util.Logging

/**
 * Find out how much simulation steps need to be executed before a suitable execution time is approximated.
 * Running too short (only few ms) means that stochastic noise is high and results may be biased.
 * Running too long means that computational resources are wasted.
 *
 * @author Roland Ewald
 */
object CalibrateSimSteps extends ExperimentAction with Logging {

  /** The required type of experiment. */
  type Experiment = AbstractExperiment with AbstractPerformanceObservation

  /** Returns approximate number of simulation steps until the desired execution time is reached. */
  def apply(experiment: Experiment, desiredExecTime: Double): Int = {
    //TODO: Add implicit ExperimentProvider
    //TODO: Add while loop etc.
    var simSteps = 1
    var runtime = 0.
    val exp = experiment.stopCondition = AfterSimSteps(simSteps)
    experiment.withExperimentPerformance {
      r => runtime = r.runtimes.head
    }
    logger.info("runtime:" + runtime)
    0
  }

}