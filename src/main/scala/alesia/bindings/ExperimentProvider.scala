package alesia.bindings

import alesia.planning.data.Problem
import sessl._
import java.net.URI

/**
 * Creates experiments for a certain simulation system.
 *
 * @author Roland Ewald
 */
trait ExperimentProvider {

  /** Create a performance experiment for the given problem. */
  def performanceExperiment: PerformanceExperiment

  /** Configures experiment for a given problem. */
  def configure[X <: AbstractExperiment](p: Problem, exp: X = performanceExperiment): X = {
    exp.model_=(p.modelURI)
    p.parameters.foreach(p => exp.set(p._1 <~ p._2))
    exp
  }
}