package alesia.planning.actions.experiments

import alesia.planning.context.ExecutionContext
import alesia.planning.domain.ParameterizedModel
import sessl.Simulator

/**
 * @author Roland Ewald
 */
case class SampleRuntimes(problem: ParameterizedModel, sim: Simulator,
  execTime: Double, eps: Double = 0.1, maxIt: Int = 20, maxFactor: Double = 10) extends ExperimentAction {

  override def execute(e: ExecutionContext) = ???

}