package alesia

import sessl.AbstractExperiment
import sessl.AbstractPerformanceObservation

/**
 * Entities to be used for ALeSiA bindings.
 * @author Roland Ewald
 */
package object bindings {

  /** A simulation algorithm. */
  type Simulator = alesia.planning.domain.Algorithm[_ <: sessl.Simulator]

  /** A performance experiment. */
  type PerformanceExperiment = AbstractExperiment with AbstractPerformanceObservation

}