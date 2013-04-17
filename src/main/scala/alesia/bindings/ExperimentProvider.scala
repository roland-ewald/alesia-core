package alesia.bindings

import alesia.planning.domain.ProblemSpaceElement
import sessl.util.Logging
import sessl._

/**
 * Creates experiments for a certain simulation system.
 *
 * @author Roland Ewald
 */
trait ExperimentProvider extends Logging {

  /** Create a performance experiment for the given problem. */
  def performanceExperiment: PerformanceExperiment

  /** Configures experiment for a given problem. */
  def performanceExperiment(p: ProblemSpaceElement, sim: Simulator): PerformanceExperiment = {
    val exp = performanceExperiment
    exp.simulator = sim.entity
    exp.model_=(p.modelURI)
    p.parameters.foreach(p => exp.set(p._1 <~ p._2))
    exp
  }

  def executeForNSteps(p: ProblemSpaceElement, s: Simulator, n: Long): Double =
    observeRuntimeFor(performanceExperiment(p, s))(_.stopCondition = AfterSimSteps(n))

  def executeForSimTime(p: ProblemSpaceElement, s: Simulator, end: Double): Double =
    observeRuntimeFor(performanceExperiment(p, s))(_.stopCondition = AfterSimTime(end))

  def observeRuntimeFor(p: ProblemSpaceElement, s: Simulator)(modifier: PerformanceExperiment => Unit): Double =
    observeRuntimeFor(performanceExperiment(p, s))(modifier)

  def observeRuntimeFor(exp: PerformanceExperiment)(modifier: PerformanceExperiment => Unit): Double = {
    var rv = 0.
    modifier(exp)
    exp.withExperimentPerformance { r => rv = r.runtimes.head }
    execute(exp)
    logger.info("Executing " + exp + ", it took " + rv + " seconds.")
    rv
  }
}