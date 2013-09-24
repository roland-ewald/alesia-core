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
import alesia.query.ProblemSpecification
import alesia.planning.actions.SimpleActionDeclaration
import alesia.query.SingleModel
import alesia.planning.actions.ActionEffect
import alesia.planning.actions.Action

/**
 * Action to introduce a single model.
 *
 * @author Roland Ewald
 */
case class SingleModelIntroduction(val url: String) extends Action {

  private[this] var result: Option[File] = None

  //TODO: Use this for reflection?
  //[ResourceProvider, SingleModelIntroduction]

  override def execute(e: ExecutionContext): ExecutionContext = {
    result = e.resources.getResourceAsFile(url)    
    e
  }

}

object SingleModelIntroductionSpecification extends ActionSpecification {

  override def preCondition: ActionFormula = !PrivateLiteral("done")

  override def effect: ActionFormula = PublicLiteral("new-model") and PrivateLiteral("done")

  override def shortName = "Load Single Model"

  override def description = "Loads a single model"

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]] = {
    if (!spec._1.exists(_.isInstanceOf[SingleModel]) || !declaredActions(this).isEmpty)
      None
    else
      Some(Seq(SimpleActionDeclaration(this, shortActionName,
        Some(!PrivateLiteral("depleted")),
        !PrivateLiteral("depleted"),
        Seq(
          ActionEffect(add = Seq(PrivateLiteral("depleted")), nondeterministic = true),
          ActionEffect(add = Seq(PublicLiteral("loadedModel")), nondeterministic = true)))))
  }
  
  override def createAction(a: ActionDeclaration, c: ExecutionContext) = {
    val selectedModel = c.entities.find(_.isInstanceOf[SingleModel])
    new SingleModelIntroduction("todo")
  }

}