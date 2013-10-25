package alesia.planning.actions.housekeeping

import java.io.File
import sessl.util.Logging
import alesia.bindings.ResourceProvider
import alesia.planning.domain.ParameterizedModel
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
import alesia.planning.execution.AddLiterals
import alesia.planning.execution.RemoveEntities
import alesia.planning.execution.NoStateUpdate

import alesia.planning.actions.SharedLiterals._

/**
 * Action to introduce a single model.
 *
 * @author Roland Ewald
 */
class SingleModelIntroduction(a: SimpleActionDeclaration) extends Action {

  val depleted = a.uniqueLiteralName("depleted")
  val loaded = a.uniqueLiteralName(loadedModel)

  override def execute(e: ExecutionContext): StateUpdate = {
    val singleModels = e.entitiesOf[SingleModel]
    if (singleModels.isEmpty) {
      StateUpdate(AddLiterals())
    } else {
      val selectedModel = selectModel(singleModels)

      val addLiterals = loaded :: (if (singleModels.size == 1) List(depleted) else Nil)

      StateUpdate.specify(
        Seq(AddLiterals(addLiterals: _*), RemoveEntities(selectedModel)),
        Map(loaded -> ParameterizedModel(selectedModel.uri)))
    }
  }

  // TODO: Support different user preferences regarding randomization?
  def selectModel(ms: Iterable[SingleModel]): SingleModel = ms.head

}

object SingleModelIntroductionSpecification extends ActionSpecification {

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
          ActionEffect(add = Seq(PublicLiteral(loadedModel)), nondeterministic = true)))))
  }

  override def createAction(a: ActionDeclaration, c: ExecutionContext) = new SingleModelIntroduction(a.asInstanceOf[SimpleActionDeclaration]) //FIME: generalize this
}