package alesia.planning.actions.experiments

import alesia.bindings.ExperimentProvider
import sessl.Simulator
import alesia.planning.domain.ParameterizedModel

/**
 * @author Roland Ewald
 */
case class SampleRuntimes(problem: ParameterizedModel, sim: Simulator,
  execTime: Double, eps: Double = 0.1, maxIt: Int = 20, maxFactor: Double = 10) extends ExperimentAction {

  override def execute(implicit provider: ExperimentProvider) {
    //TODO
  }

}