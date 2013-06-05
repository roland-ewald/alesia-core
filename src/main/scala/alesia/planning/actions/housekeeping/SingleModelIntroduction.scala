package alesia.planning.actions.housekeeping

import java.io.File
import alesia.bindings.ResourceProvider
import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.AllDeclaredActions
import alesia.planning.actions.PrivateLiteral
import alesia.planning.actions.PublicLiteral
import alesia.planning.context.ExecutionContext
import alesia.query.UserSpecification
import alesia.planning.actions.SimpleActionDeclaration
import alesia.query.SingleModel

/**
 * Action to introduce a single model.
 *
 * @author Roland Ewald
 */
case class SingleModelIntroduction(val url: String) extends ModelIntroduction {

  private[this] var result: Option[File] = None

  //TODO: Use this for reflection?
  //[ResourceProvider, SingleModelIntroduction]

  override def execute(implicit provider: ResourceProvider): Unit = {
    result = provider.getResourceAsFile(url)
  }

  override def resultFor(key: String): Option[AnyRef] = None

}

object SingleModelIntroductionSpecification extends ActionSpecification {

  override def preCondition: ActionFormula = !PrivateLiteral("done")

  override def effect: ActionFormula = PublicLiteral("new-model") and PrivateLiteral("done")

  override def shortName = "Load Single Model"

  override def description = "Loads a single model"

  override def declareConcreteActions(spec: UserSpecification, declaredActions: AllDeclaredActions): Seq[ActionDeclaration] = {
    if (spec._1.exists(_.isInstanceOf[SingleModel]) && declaredActions(this).isEmpty) {
      Seq(SimpleActionDeclaration(shortActionName, preCondition = !PrivateLiteral("depleted"),
        effect = PrivateLiteral("depleted") or PublicLiteral("loadedModel")))
    } else
      Seq()
  }

  override def createAction(logicalName: String, c: ExecutionContext) = new SingleModelIntroduction("todo") //TODO

}