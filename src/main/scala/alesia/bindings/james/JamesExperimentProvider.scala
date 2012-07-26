package alesia.bindings.james

import alesia.bindings.ExperimentProvider
import alesia.planning.data.Problem

import sessl._
import sessl.james._

/**
 * Provider for JAMES II experiments.
 *
 * @author Roland Ewald
 */
object JamesExperimentProvider extends ExperimentProvider {

  override def performanceExperiment = new Experiment with PerformanceObservation

}