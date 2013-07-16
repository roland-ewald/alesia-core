package alesia.experiments.configuration.frace

trait ExecutionEnvironment {
  
  def measurePerformance(simulator: Configuration, model: ModelSetup): Double 

}