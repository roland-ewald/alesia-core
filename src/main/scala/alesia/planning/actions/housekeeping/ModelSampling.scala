package alesia.planning.actions.housekeeping

import alesia.planning.actions.Action
import alesia.planning.context.ExecutionContext
import alesia.planning.execution.StateUpdate
import alesia.query.SingleModel
import alesia.planning.actions.ActionDeclaration
import alesia.planning.execution.AddLiterals
import alesia.query.ProblemSpecification
import alesia.query.ModelSet
import alesia.planning.actions.SimpleActionDeclaration
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.AllDeclaredActions
import alesia.planning.execution.RemoveEntities
import alesia.planning.actions.PrivateLiteral
import alesia.planning.actions.ActionEffect
import alesia.planning.actions.SharedLiterals._
import alesia.planning.domain.ParameterizedModel
import alesia.planning.actions.PublicLiteral
import alesia.query.ModelParameter
import alesia.utils.misc.CollectionHelpers.filterType
import org.jamesii.core.math.random.RandomSampler
import org.jamesii.core.math.random.generators.java.JavaRandom

/**
 * Action to sample a single model.
 *
 * @author Roland Ewald
 */
class ModelSampling(a: SimpleActionDeclaration, sd: SamplingData) extends Action {

  val depleted = a.uniqueLiteralName(ModelSamplingSpecification.depletedName(sd.modelSet))
  val loaded = a.uniqueLiteralName(loadedModel)
  val maxSampleTrials = 100
  val rng = new JavaRandom() //TODO: Use common RNG

  override def execute(e: ExecutionContext): StateUpdate = {
    val newSample = createSample()
    if (newSample.isDefined) {
      sd.pastSamples += newSample.get
      StateUpdate.specify(Seq(AddLiterals(loaded)), Map(loaded -> ParameterizedModel(sd.modelSet.setURI, newSample.get)))
    } else {
      StateUpdate.specify(Seq(AddLiterals(depleted)))
    }
  }

  def createSample(): Option[Map[String, AnyVal]] = {
    var newSample = sampleModel(sd.modelSet)
    var currentTrial = 1
    while (sd.pastSamples.contains(newSample) && currentTrial < maxSampleTrials) {
      newSample = sampleModel(sd.modelSet)
      currentTrial += 1
    }
    if (currentTrial == maxSampleTrials)
      None
    else Some(newSample)
  }

  def sampleModel(ms: ModelSet): Map[String, AnyVal] = {
    val assignment = for (p <- ms.params) yield (p.name, sampleParameter(p))
    assignment.toMap
  }

  def sampleParameter(p: ModelParameter[_ <: AnyVal]): AnyVal = p match {
    case ModelParameter(_, lower: Int, step: Int, upper: Int) => {
      val result = RandomSampler.sample(1, lower, upper + 1, rng) //Upper bound for sampler seems to be exclusive
      result.get(0).toInt
    }
    case ModelParameter(_, lower: Double, step: Double, upper: Double) => ??? //TODO: Finish this!
    case _ => ???
  }

}

class SamplingData(val modelSet: ModelSet) {
  val pastSamples = scala.collection.mutable.Set[Map[String, AnyVal]]() //TODO: Use immutable data structure here
}

object ModelSamplingSpecification extends ActionSpecification {

  def depletedName(ms: ModelSet): String = "depleted_" + ms.setURI

  override def shortName = "Sample Model"

  override def description = "Sample a model from a model set"

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]] = {

    val modelSets = filterType[ModelSet](spec._1)

    if (!declaredActions(this).isEmpty || modelSets.isEmpty) {
      None
    } else {
      val actions = for (set <- modelSets) yield {
        val depleted = depletedName(set)
        SimpleActionDeclaration(this, shortActionName,
          Seq((depleted, false)),
          !PrivateLiteral(depleted),
          Seq(
            ActionEffect(add = Seq(PrivateLiteral(depleted)), nondeterministic = true),
            ActionEffect(add = Seq(PublicLiteral(loadedModel)), nondeterministic = true)),
          actionSpecifics = Some(new SamplingData(set)))
      }
      Some(actions)
    }
  }

  override def createAction(a: ActionDeclaration, c: ExecutionContext) =
    a match {
      case s: SimpleActionDeclaration =>
        s.actionSpecifics match {
          case Some(sd: SamplingData) => new ModelSampling(s, sd)
          case _ => ???
        }
      case _ => ???
    }

}