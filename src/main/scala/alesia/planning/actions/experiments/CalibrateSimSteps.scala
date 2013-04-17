package alesia.planning.actions.experiments

import alesia.bindings.ExperimentProvider
import sessl.util.Logging
import sessl.AfterSimSteps
import alesia.bindings.Simulator
import alesia.planning.domain.ProblemSpaceElement
import scala.math._

/**
 * Find out how much simulation steps need to be executed before a suitable execution time is approximated.
 *  Running too short (only few ms) means that stochastic noise is high and results may be biased.
 *  Running too long means that computational resources are wasted.
 *
 *  @param p
 *          the problem
 *  @param a
 *          the algorithm
 *  @param execTime
 *          the desired execution time
 *  @param eps
 *          the acceptable relative deviation from the desired execution time, e.g. epsilon = 0.1 means +/- 10% deviation is OK
 *  @param maxIt
 *          the maximal number of iterations
 *  @param maxFactor
 *          the maximal factor by which the number of steps will be increased
 *
 *  @author Roland Ewald
 */
case class CalibrateSimSteps(problem: ProblemSpaceElement, sim: Simulator,
  execTime: Double, eps: Double = 0.1, maxIt: Int = 20, maxFactor: Double = 10) extends ExperimentAction with Logging {

  val result = "result"

  /**
   * Execute the action.
   *  @param provider
   *          the experiment provider
   *  @return the tuple (#steps, achieved runtime)
   */
  override def execute(implicit pr: ExperimentProvider): Unit = {
    require(execTime > 0, "Desired execution time has to be positive.")
    require(eps > 0 && eps < 1, "Epsilon should be in (0,1).")
    require(maxIt > 1, "The maximal number of iterations should be > 1.")
    require(maxFactor > 1, "The maximal multiplication factor should be > 1.")

    var steps: Long = 1
    var runtime = pr.executeForNSteps(problem, sim, steps)
    var counter = 1

    while (counter < maxIt && (runtime <= (1 - eps) * execTime || runtime >= (1 + eps) * execTime)) {
      steps = round(steps * min(maxFactor, execTime / runtime))
      runtime = pr.executeForNSteps(problem, sim, steps)
      counter += 1
    }

    addResult(result, (steps, runtime))
  }

}