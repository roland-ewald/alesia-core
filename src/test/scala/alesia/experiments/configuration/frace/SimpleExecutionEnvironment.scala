package alesia.experiments.configuration.frace

import org.jamesii.SimSystem
import org.jamesii.core.math.random.distributions.NormalDistribution

object SimpleExecutionEnvironment extends ExecutionEnvironment {

  val rng = SimSystem.getRNGGenerator().getNextRNG()

  val difficulty = 1

  override def measurePerformance(simulator: Configuration, model: ModelSetup): Double = {
    val mean = ((simulator._1 + model._1) % 10) + 5 + (simulator._1 / difficulty)
    val normDist = new NormalDistribution(rng, mean, 0.1 * mean)
    normDist.getRandomNumber()
  }

}