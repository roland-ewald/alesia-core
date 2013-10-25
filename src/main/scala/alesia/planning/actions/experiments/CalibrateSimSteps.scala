package alesia.planning.actions.experiments

import scala.language.existentials
import alesia.bindings.ExperimentProvider
import sessl.util.Logging
import sessl.AfterSimSteps
import alesia.bindings.Simulator
import scala.math._
import alesia.planning.domain.ParameterizedModel
import alesia.planning.context.ExecutionContext
import alesia.planning.execution.NoStateUpdate
import alesia.planning.actions.ActionDeclaration
import alesia.query.ProblemSpecification
import alesia.query.SingleSimulator
import alesia.planning.actions.SimpleActionDeclaration
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.PublicLiteral
import alesia.planning.actions.AllDeclaredActions
import alesia.utils.misc.CollectionHelpers
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.ActionEffect
import sessl.james.NextReactionMethod
import alesia.planning.actions.SharedLiterals._

/**
 * Find out how much simulation steps need to be executed before a suitable execution time is approximated.
 *  Running too short (only few ms) means that stochastic noise is high and results may be biased.
 *  Running too long means that computational resources are wasted.
 *
 *  @param problem
 *          the problem
 *  @param sim
 *          the simulator
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
case class CalibrateSimSteps(problem: ParameterizedModel, sim: Simulator,
  execTime: Double, eps: Double = 0.1, maxIt: Int = 20, maxFactor: Double = 10) extends ExperimentAction with Logging {

  val result = "calibrated-model-for-sim"

  /**
   * Execute the action.
   *  @param e the execution context
   *  @return the tuple (#steps, achieved runtime)
   */
  override def execute(e: ExecutionContext) = {
    require(execTime > 0, "Desired execution time has to be positive.")
    require(eps > 0 && eps < 1, "Epsilon should be in (0,1).")
    require(maxIt > 1, "The maximal number of iterations should be > 1.")
    require(maxFactor > 1, "The maximal multiplication factor should be > 1.")

    var steps: Long = 1
    var runtime = e.experiments.executeForNSteps(problem, sim, steps)
    var counter = 1

    while (counter < maxIt && (runtime <= (1 - eps) * execTime || runtime >= (1 + eps) * execTime)) {
      steps = round(steps * min(maxFactor, execTime / runtime))
      runtime = e.experiments.executeForNSteps(problem, sim, steps)
      counter += 1
    }

    addResult(result, (problem, sim, steps, runtime))
    NoStateUpdate //FIXME
  }

}

object CalibrateSimStepsSpecification extends ActionSpecification {

  val calibratedModel = PublicLiteral(property("calibrated", loadedModel))

  override def preCondition: ActionFormula = PublicLiteral(loadedModel)

  override def effect: ActionFormula = (calibratedModel or !calibratedModel) and !preCondition

  override def shortName = "Calibrate Model"

  override def description = "Checks whether a model can be calibrated."

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]] = {
//    if (declaredActions(this).nonEmpty)
      None
    /*else Some(
      Seq(SimpleActionDeclaration(this, shortActionName, Seq(), preCondition, Seq(
        ActionEffect(add = Seq(calibratedModel), nondeterministic = true),
        ActionEffect(del = Seq(calibratedModel), nondeterministic = true)))))*/
  }

  override def createAction(a: ActionDeclaration, c: ExecutionContext) = {

    import CollectionHelpers._

    val models = filterType[ParameterizedModel](c.entitiesForLiterals(loadedModel))
    require(models.nonEmpty, s"No parameterized model linked to '${loadedModel}'")

    val simulators = filterType[SingleSimulator](c.entities)
    require(simulators.nonEmpty, s"No single simulator defined.")

    val maxExecTime: Double = 5.0 //FIXME: generalize this

    ???
  }

}