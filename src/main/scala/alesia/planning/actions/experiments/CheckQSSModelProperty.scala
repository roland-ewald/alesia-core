package alesia.planning.actions.experiments

import scala.language.existentials
import alesia.bindings.ExperimentProvider
import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.PublicLiteral
import alesia.planning.context.ExecutionContext
import alesia.planning.domain.ParameterizedModel
import sessl.AfterWallClockTime
import sessl.util.Logging
import alesia.planning.actions.AllDeclaredActions
import alesia.query.ProblemSpecification
import alesia.planning.actions.SimpleActionDeclaration
import alesia.bindings.Simulator
import alesia.planning.actions.ActionEffect
import alesia.planning.execution.NoStateUpdate

/**
 * Checks whether this model exhibits a quasi-steady state property and, if so, from which point in simulation time on.
 *
 * @author Roland Ewald
 */
case class CheckQSSModelProperty(problem: ParameterizedModel, sim: Simulator,
  maxExecTimeSeconds: Double, linearSteps: Int = 3, errorAllowed: Double = 0.1) extends ExperimentAction with Logging {

  override def execute(e:ExecutionContext) = {
    val pessimisticStepRuntime = e.experiments.executeForNSteps(problem, sim, 1)
    val pessimisticMaxSteps = math.round(maxExecTimeSeconds / pessimisticStepRuntime)
    require(pessimisticMaxSteps > 10, "Maximal execution time is too small, same magnitude as the minimal time of ${pessimisticStepRuntime} (executing a single step).")

    var runtime = e.experiments.observeRuntimeFor(problem, sim) { exp =>
      val ms = math.round((maxExecTimeSeconds - maxExecTimeSeconds.toInt) * 1000L)
      exp.stopCondition = AfterWallClockTime(seconds = maxExecTimeSeconds.toInt, milliseconds = ms.toInt)
    }

    NoStateUpdate
    //TODO: check if last three--five points form a line???
  }

}

object QSSModelPropertyCheckSpecification extends ActionSpecification {

  private val qss = PublicLiteral("qss(loadedModel)")

  override def preCondition: ActionFormula = PublicLiteral("loadedModel") //TODO: and PublicLiteral("loadedSimulator")

  override def effect: ActionFormula = qss or !qss

  override def shortName = "Check QSS for Model"

  override def description = "Checks whether a model has a quasi-steady state (useful for performance comparisons)."

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]] = {
    if (declaredActions(this).nonEmpty)
      None
    else Some(
      Seq(SimpleActionDeclaration(this, shortActionName, None, preCondition, Seq(
        ActionEffect(add = Seq(qss), nondeterministic = true),
        ActionEffect(del = Seq(qss), nondeterministic = true)))))
  }

  override def createAction(a: ActionDeclaration, c: ExecutionContext) = ??? //new CheckQSSModelProperty("todo") //TODO

}