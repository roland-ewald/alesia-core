package alesia.planning.actions.experiments

import scala.language.existentials
import scala.math._
import alesia.bindings.ExperimentProvider
import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.PublicLiteral
import alesia.planning.actions.SharedLiterals._
import alesia.planning.actions.SharedLiterals
import alesia.planning.context.ExecutionContext
import alesia.planning.domain.ParameterizedModel
import alesia.planning.execution.StateUpdate
import alesia.query.MaxSingleExecutionWallClockTime
import alesia.query.SingleSimulator
import alesia.utils.misc.CollectionHelpers
import sessl.util.Logging
import alesia.planning.actions.AllDeclaredActions
import alesia.planning.execution.AddLiterals
import alesia.query.ProblemSpecification
import alesia.bindings.Simulator
import alesia.query.UserDomainEntity
import alesia.planning.actions.SimpleActionDeclaration
import alesia.planning.actions.ActionEffect

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
case class CalibrateSimSteps(problem: ParameterizedModel, sims: Seq[Simulator],
  execTime: Double, eps: Double = 0.1, maxIt: Int = 20, maxFactor: Double = 10) extends ExperimentAction with Logging {

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
    require(sims.nonEmpty, "At least one simultor needs to be defined.")

    val results = sims.map { s =>
      val calibration = runCalibration(s, e)
      CalibrationResult(problem, s, calibration._1, calibration._2)
    }

    StateUpdate.specify(Seq(AddLiterals(calibratedModel)), Map(calibratedModel -> CalibrationResults(results)))
  }

  private[this] def runCalibration(sim: Simulator, e: ExecutionContext): (Long, Double) = {
    var steps = 1L
    var runtime = e.experiments.executeForNSteps(problem, sim, steps)
    var counter = 1

    while (counter < maxIt && (runtime <= (1 - eps) * execTime || runtime >= (1 + eps) * execTime)) {
      steps = round(steps * min(maxFactor, execTime / runtime))
      runtime = e.experiments.executeForNSteps(problem, sim, steps)
      counter += 1
    }
    (steps, runtime)
  }

}

case class CalibrationResult(problem: ParameterizedModel, sim: Simulator, steps: Long, runtime: Double)

case class CalibrationResults(results: Seq[CalibrationResult]) extends UserDomainEntity {
  require(results.nonEmpty, "No empty results permitted.")
  require(results.forall(_.problem == results.head.problem), "Only results for the same model can be stored together.")
}

object CalibrateSimStepsSpecification extends ActionSpecification {

  val calibrated = PublicLiteral(calibratedModel)

  val defaultMaxExecWCT = MaxSingleExecutionWallClockTime(seconds = 10)

  override def shortName = "Calibrate Model"

  override def description = "Calibrates a model can be calibrated."

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]] = {
    singleAction(declaredActions) {
      SimpleActionDeclaration(this, shortActionName, Seq(), PublicLiteral(loadedModel), Seq(
        ActionEffect(add = Seq(calibrated), nondeterministic = false)))
    }
  }

  override def createAction(a: ActionDeclaration, c: ExecutionContext) = {

    import CollectionHelpers._

    val models = filterType[ParameterizedModel](c.entitiesForLiterals(loadedModel))
    require(models.nonEmpty, s"No parameterized model linked to '${loadedModel}'")

    val simulators = filterType[SingleSimulator](c.entities)
    require(simulators.nonEmpty, s"No single simulator defined.")

    val maxExecTime: Double = getOrElse[MaxSingleExecutionWallClockTime](c.preferences, defaultMaxExecWCT).asSecondsOrUnitless

    CalibrateSimSteps(models.head, simulators, maxExecTime)
  }

}