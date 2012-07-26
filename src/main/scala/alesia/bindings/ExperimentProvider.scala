package alesia.bindings

import alesia.planning.domain.Problem
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
  def performanceExperiment(p: Problem, sim: Simulator): PerformanceExperiment = {
    val exp = performanceExperiment
    exp.simulator = sim.entity
    exp.model_=(p.modelURI)
    p.parameters.foreach(p => exp.set(p._1 <~ p._2))
    exp
  }
}