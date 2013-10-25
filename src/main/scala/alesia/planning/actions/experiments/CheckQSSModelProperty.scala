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

/**
 * Checks whether a pre-calibrated model exhibits a quasi-steady state property.
 *
 * @author Roland Ewald
 */
case class CheckQSSModelProperty(problem: ParameterizedModel, sim: Simulator,
  maxExecTimeSeconds: Double, linearSteps: Int = 3, errorAllowed: Double = 0.2) extends ExperimentAction with Logging {

  import QSSModelPropertyCheckSpecification._

  override def execute(e: ExecutionContext) = {
    val pessimisticStepRuntime = e.experiments.executeForNSteps(problem, sim, 1)
    val pessimisticMaxSteps = math.round(maxExecTimeSeconds / pessimisticStepRuntime)
    require(pessimisticMaxSteps > 10, "Maximal execution time is too small, same magnitude as the minimal time of ${pessimisticStepRuntime} (executing a single step).")

    var runtime = e.experiments.observeRuntimeFor(problem, sim) { exp =>
      val ms = math.round((maxExecTimeSeconds - maxExecTimeSeconds.toInt) * 1000L)
      exp.stopCondition = AfterWallClockTime(seconds = maxExecTimeSeconds.toInt, milliseconds = ms.toInt)
    }

    val hasQSS = true //FIXME: check if last three--five points form a line???

    if (hasQSS)
      StateUpdate.specify(Seq(AddLiterals(qss.name)), Map(qss.name -> problem))
    else
      StateUpdate.specify(Seq(RemoveLiterals(qss.name)), Map(), Map(loadedModel -> problem))

  }

}

object QSSModelPropertyCheckSpecification extends ActionSpecification {

  val qss = PublicLiteral(property("qss", loadedModel))

  val defaultMaxExecWCT = MaxSingleExecutionWallClockTime(seconds = 4)

  override def shortName = "Check QSS for Model"

  override def description = "Checks whether a model has a quasi-steady state (useful for performance comparisons)."

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]] =
    singleAction(declaredActions) {
      SimpleActionDeclaration(this, shortActionName, Seq(), PublicLiteral(loadedModel), Seq(
        ActionEffect(add = Seq(qss), nondeterministic = true),
        ActionEffect(del = Seq(qss), nondeterministic = true)))
    }

  override def createAction(a: ActionDeclaration, c: ExecutionContext) = {
    import CollectionHelpers._

    val models = filterType[ParameterizedModel](c.entitiesForLiterals(loadedModel))
    require(models.nonEmpty, s"No parameterized model linked to '${loadedModel}'")

    val simulators = filterType[SingleSimulator](c.entities)
    require(simulators.nonEmpty, s"No single simulator defined.")

    val maxExecTime: Double = getOrElse[MaxSingleExecutionWallClockTime](c.preferences, defaultMaxExecWCT).asSecondsOrUnitless
    CheckQSSModelProperty(models.head, simulators.head, maxExecTime)
  }

}