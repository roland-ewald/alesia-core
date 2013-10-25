package alesia.planning.actions.experiments

import scala.language.existentials
import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.ActionEffect
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.PublicLiteral
import alesia.planning.actions.SharedLiterals._
import alesia.planning.actions.SimpleActionDeclaration
import alesia.planning.context.ExecutionContext
import alesia.planning.domain.Algorithm
import alesia.planning.domain.ParameterizedModel
import alesia.planning.execution.AddLiterals
import alesia.planning.execution.RemoveLiterals
import alesia.planning.execution.StateUpdate
import alesia.query.SingleSimulator
import alesia.query.UserPreference
import alesia.utils.misc.CollectionHelpers
import sessl.AfterWallClockTime
import sessl.util.Logging
import alesia.query.MaxSingleExecutionWallClockTime
import alesia.planning.actions.AllDeclaredActions
import alesia.query.ProblemSpecification
import alesia.bindings.Simulator
import alesia.query.UserDomainEntity
import alesia.query.MaxAllowedQSSError

/**
 * Checks whether a pre-calibrated model exhibits a quasi-steady state property.
 *
 * @author Roland Ewald
 */
case class CheckQSSModelProperty(calibration: CalibrationResults, maxExecTimeSeconds: Double, errorAllowed: Double,
  linearSteps: Int = 3) extends ExperimentAction with Logging {

  val lowerBound = 1 - errorAllowed / 2
  val upperBound = 1 + errorAllowed / 2

  import QSSModelPropertyCheckSpecification._
  import CollectionHelpers._

  override def execute(e: ExecutionContext) = {

    val problem = calibration.results.head.problem
    val results = calibration.results.map(checkQSS(_, e))

    if (results.forall(_.hasQSS))
      StateUpdate.specify(Seq(AddLiterals(qss.name)), Map(qss.name -> QSSCheckResults(results)))
    else
      StateUpdate.specify(Seq(RemoveLiterals(qss.name)), Map(qss.name -> QSSCheckResults(results)))
  }

  def checkQSS(c: CalibrationResult, e: ExecutionContext): QSSCheckResult = {

    val runtimes = for (stepNum <- 1 to linearSteps) yield {
      (stepNum, e.experiments.executeForNSteps(c.problem, c.sim, stepNum * c.steps))
    }

    val normalRuntime = runtimes.head._2

    val slowDownRatioErrors = runtimes.map { run =>
      val actualSlowDownRatio = run._2 / normalRuntime
      actualSlowDownRatio / run._1
    }

    val hasQSS = slowDownRatioErrors.forall(x => x >= lowerBound && x <= upperBound)

    QSSCheckResult(c.problem, c.sim, hasQSS, runtimes, slowDownRatioErrors)
  }

}

case class QSSCheckResult(problem: ParameterizedModel, sim: Simulator, hasQSS: Boolean, runtimes: Seq[(Int, Double)], slowDownRatioErrors: Seq[Double])

case class QSSCheckResults(results: Seq[QSSCheckResult]) extends UserDomainEntity

object QSSModelPropertyCheckSpecification extends ActionSpecification {

  val qss = PublicLiteral(property("qss", loadedModel))

  val defaultMaxExecWCT = MaxSingleExecutionWallClockTime(seconds = 4)

  override def shortName = "Check QSS for Model"

  override def description = "Checks whether a model has a quasi-steady state (useful for performance comparisons)."

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]] =
    singleAction(declaredActions) {
      SimpleActionDeclaration(this, shortActionName, Seq(), PublicLiteral(calibratedModel), Seq(
        ActionEffect(add = Seq(qss), nondeterministic = true),
        ActionEffect(del = Seq(qss), nondeterministic = true)))
    }

  override def createAction(a: ActionDeclaration, c: ExecutionContext) = {
    import CollectionHelpers._

    val calibrationResults = filterType[CalibrationResults](c.entitiesForLiterals(calibratedModel)).headOption
    require(calibrationResults.isDefined, "No calibration results are defined")

    val maxExecTime = getOrElse[MaxSingleExecutionWallClockTime](c.preferences, defaultMaxExecWCT).asSecondsOrUnitless
    val error = getOrElse[MaxAllowedQSSError](c.preferences, MaxAllowedQSSError()).relativeError
    CheckQSSModelProperty(calibrationResults.get, maxExecTime, error)
  }

}