package alesia.planning.actions.housekeeping

import alesia.bindings.ResourceProvider
import java.io.File
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.PublicLiteral
import alesia.planning.context.ExecutionContext

/**
 * Action to introduce a single model.
 * @author Roland Ewald
 */
case class SingleModel(val url: String) extends ModelIntroduction {

  private[this] var result: Option[File] = None

  override def execute(implicit provider: ResourceProvider): Unit = {
    result = provider.getResourceAsFile(url)
  }

  override def resultFor(key: String): Option[AnyRef] = None

}

object SingleModelSpecification extends ActionSpecification[ResourceProvider, SingleModel] {

  def preCondition: Option[ActionFormula] = None

  def effect: ActionFormula = PublicLiteral("model-introduced")

  def publicLiterals = Seq() //TODO: Provide default implementations in separate type

  def privateLiterals = Seq() //TODO 

  def createAction(logicalName: String, c: ExecutionContext) = new SingleModel("dfsdfdsf")
}