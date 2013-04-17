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

  def executeForNSteps(p: ProblemSpaceElement, s: Simulator, n: Long): Double = {
    var rv = 0.
    val exp = performanceExperiment(p, s)
    exp.stopCondition = AfterSimSteps(n)
    exp.withExperimentPerformance { r => rv = r.runtimes.head }
    execute(exp)
    logger.info("Executing " + s + "on " + p + " for " + s + " steps: it took " + rv + " seconds.")
    rv
  }
}