package alesia.planning.actions.experiments

import scala.language.existentials
import sessl.util.Logging
import alesia.bindings.ExperimentProvider
import alesia.bindings.Simulator
import sessl.AfterWallClockTime
import sessl.AfterSimSteps
import alesia.planning.domain.ParameterizedModel
import alesia.planning.actions.ActionDeclaration
import alesia.query.ProblemSpecification
import alesia.planning.actions.housekeeping.SingleModelIntroduction
import alesia.planning.actions.SimpleActionDeclaration
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.PublicLiteral
import alesia.planning.actions.AllDeclaredActions
import alesia.planning.actions.PrivateLiteral
import alesia.planning.actions.ActionFormula
import alesia.planning.context.ExecutionContext
import alesia.query.SingleModel

/**
 * Checks whether this model exhibits a quasi-steady state property and, if so, from which point in simulation time on.
 *
 * @author Roland Ewald
 */
case class CheckQSSModelProperty(problem: ParameterizedModel, sim: Simulator,
  maxExecTimeSeconds: Double, linearSteps: Int = 3, errorAllowed: Double = 0.1) extends ExperimentAction with Logging {

  override def execute(implicit pr: ExperimentProvider) {
    val pessimisticStepRuntime = pr.executeForNSteps(problem, sim, 1)
    val pessimisticMaxSteps = math.round(maxExecTimeSeconds / pessimisticStepRuntime)
    require(pessimisticMaxSteps > 10, "Maximal execution time is too small, same magnitude as the minimal time of ${pessimisticStepRuntime} (executing a single step).")

    var runtime = pr.observeRuntimeFor(problem, sim) { exp =>
      val ms = math.round((maxExecTimeSeconds - maxExecTimeSeconds.toInt) * 1000L)
      exp.stopCondition = AfterWallClockTime(seconds = maxExecTimeSeconds.toInt, milliseconds = ms.toInt)
    }

    //TODO: check if last three--five points form a line???
  }

}

object QSSModelPropertyCheckSpecification extends ActionSpecification {

  private val qss = PublicLiteral("qss(loadedModel)")

  override def preCondition: ActionFormula = PublicLiteral("loadedModel") and PublicLiteral("loadedSimulator")

  override def effect: ActionFormula = qss or !qss

  override def shortName = "Check QSS for Model"

  override def description = "Checks whether a model has a quasi-steady state (useful for performance comparisons)."

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Seq[ActionDeclaration] = {
    if (declaredActions(this).isEmpty) {
      Seq(SimpleActionDeclaration(shortActionName, preCondition, effect))
    } else
      Seq()
  }

  override def createAction(logicalName: String, c: ExecutionContext) = ??? //new CheckQSSModelProperty("todo") //TODO

}