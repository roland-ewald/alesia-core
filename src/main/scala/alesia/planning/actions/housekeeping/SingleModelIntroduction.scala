package alesia.planning.actions.housekeeping

import java.io.File

import alesia.bindings.ResourceProvider
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.PrivateLiteral
import alesia.planning.actions.PublicLiteral
import alesia.planning.context.ExecutionContext
import alesia.query.SingleModel
import alesia.query.UserSpecification

/**
 * Action to introduce a single model.
 * @author Roland Ewald
 */
case class SingleModelIntroduction(val url: String) extends ModelIntroduction {

  private[this] var result: Option[File] = None

  override def execute(implicit provider: ResourceProvider): Unit = {
    result = provider.getResourceAsFile(url)
  }

  override def resultFor(key: String): Option[AnyRef] = None

}

object SingleModelIntroductionSpecification extends ActionSpecification[ResourceProvider, SingleModelIntroduction] {

  override def preCondition: ActionFormula = !PrivateLiteral("done")

  override def effect: ActionFormula = PublicLiteral("new-model") and PrivateLiteral("done")

  override def publicLiterals = Seq() //TODO: Provide default implementations in separate type

  override def privateLiterals = Seq() //TODO 

  override def shortName = "Load Single Model"

  override def description = "Loads a single model"

  override def suitableFor(u: UserSpecification) = u._1.exists(_.isInstanceOf[SingleModel])

  override def createAction(logicalName: String, c: ExecutionContext) = new SingleModelIntroduction("dfsdfdsf")

}