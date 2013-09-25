package alesia.planning.actions.housekeeping

import java.io.File
import sessl.util.Logging
import alesia.bindings.ResourceProvider
import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.AllDeclaredActions
import alesia.planning.actions.PrivateLiteral
import alesia.planning.actions.PublicLiteral
import alesia.planning.context.ExecutionContext
import alesia.query.ProblemSpecification
import alesia.planning.actions.SimpleActionDeclaration
import alesia.query.SingleModel
import alesia.planning.actions.ActionEffect
import alesia.planning.actions.Action
import alesia.planning.execution.StateUpdate
import alesia.planning.execution.SimpleStateUpdate
import alesia.planning.execution.Change
import alesia.planning.execution.NoStateUpdate

/**
 * Action to introduce a single model.
 *
 * @author Roland Ewald
 */
class SingleModelIntroduction extends Action with Logging {

  override def execute(e: ExecutionContext): StateUpdate = {
    val singleModels = e.entities.collect { case s: SingleModel => s }
    if (singleModels.isEmpty) {
      SimpleStateUpdate(
        Change(Seq("depleted")))
    } else {
      val selectedModel = selectModel(singleModels)
      SimpleStateUpdate(
        Change(Seq("loadedModel"), Seq(selectedModel)))
    }
  }

  // TODO: Support different user preferences regarding randomization?
  def selectModel(ms: Seq[SingleModel]): SingleModel = ms.head

}

object SingleModelIntroductionSpecification extends ActionSpecification {

  override def preCondition: ActionFormula = !PrivateLiteral("done")

  override def effect: ActionFormula = PublicLiteral("new-model") and PrivateLiteral("done")

  override def shortName = "Load Single Model"

  override def description = "Loads a single model"

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]] = {
    if (!declaredActions(this).isEmpty || !spec._1.exists(_.isInstanceOf[SingleModel]))
      None
    else
      Some(Seq(SimpleActionDeclaration(this, shortActionName,
        Seq(("depleted", false)),
        !PrivateLiteral("depleted"),
        Seq(
          ActionEffect(add = Seq(PrivateLiteral("depleted")), nondeterministic = true),
          ActionEffect(add = Seq(PublicLiteral("loadedModel")), nondeterministic = true)))))
  }

  override def createAction(a: ActionDeclaration, c: ExecutionContext) = new SingleModelIntroduction
}