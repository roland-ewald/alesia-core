package alesia.planning.actions.experiments

import alesia.bindings.ExperimentProvider
import alesia.planning.domain.ProblemSpaceElement
import sessl.Simulator

/**
 * @author Roland Ewald
 */
case class SampleRuntimes(problem: ProblemSpaceElement, sim: Simulator,
  execTime: Double, eps: Double = 0.1, maxIt: Int = 20, maxFactor: Double = 10) extends ExperimentAction {

  override def execute(implicit provider: ExperimentProvider) {
    //TODO
  }

}