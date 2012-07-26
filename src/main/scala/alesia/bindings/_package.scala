package alesia

import sessl.AbstractExperiment
import sessl.AbstractPerformanceObservation

/**
 * Entities to be used for ALeSiA bindings. 
 * @author Roland Ewald
 */
package object bindings {

  /** A performance experiment. */
  type PerformanceExperiment = AbstractExperiment with AbstractPerformanceObservation

}