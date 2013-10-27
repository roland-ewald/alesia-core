package alesia.planning.actions.experiments

import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.ActionEffect
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.PublicLiteral
import alesia.planning.actions.SharedLiterals._
import alesia.planning.actions.SimpleActionDeclaration
import alesia.planning.context.ExecutionContext
import alesia.planning.execution.AddLiterals
import alesia.planning.execution.StateUpdate
import alesia.query.SingleSimulator
import alesia.query.SingleSimulator
import alesia.utils.misc.CollectionHelpers
import alesia.utils.misc.CollectionHelpers._
import alesia.planning.actions.AllDeclaredActions
import alesia.query.ProblemSpecification
import alesia.planning.actions.SimpleActionDeclaration
import alesia.planning.domain.ParameterizedModel
import org.jamesii.core.math.statistics.tests.wilcoxon.WilcoxonRankSumTest
import org.jamesii.core.math.statistics.univariate.ArithmeticMean
import alesia.query.UserDomainEntity
import alesia.planning.execution.RemoveLiterals

/**
 * Compare two (calibrated) simulators with each other.
 *
 * TODO: Simplify this: the simulator with the alphabetically smaller string representation
 * is always the first one (needs to be adhered to globally). Then, remove then-obsolete second similarTo-Literal.
 *
 * @author Roland Ewald
 */
case class CompareSimulators(simA: SingleSimulator, simB: SingleSimulator,
  replicationsPerConfig: Int = 20, alpha: Double = 0.05) extends ExperimentAction {

  //FIXME: This needs to be handled automatically by the underlying system
  val temporaryLiteralFix = RemoveLiterals(calibratedModel, loadedModel, property("qss", loadedModel))

  override def execute(e: ExecutionContext) = {

    import scala.collection.JavaConversions._
    import CompareSimulatorsSpecification._

    def resultFor(s: SingleSimulator)(r: CalibrationResult) = r.sim == s

    def execute(p: ParameterizedModel, s: SingleSimulator, steps: Long): Seq[Double] =
      (1 to replicationsPerConfig).map(_ => e.experiments.executeForNSteps(p, s, steps))

    val entities = filterType[CalibrationResults](e.entitiesForLiterals(calibratedModel))

    val suitableCalibrationResults = entities.find { cr =>
      cr.results.exists(resultFor(simA)_) && cr.results.exists(resultFor(simB)_)
    }

    require(suitableCalibrationResults.isDefined, s"No suitable calibration results found to compare ${simA} and ${simB}")

    val singleCalibrationResults = suitableCalibrationResults.get.results

    val problem = singleCalibrationResults.head.problem
    val maxSteps = singleCalibrationResults.filter(r => resultFor(simA)(r) || resultFor(simB)(r)).map(_.steps).max

    val runtimesA = execute(problem, simA, maxSteps)
    val runtimesB = execute(problem, simB, maxSteps)

    val pValue = new WilcoxonRankSumTest().executeTest(seqAsJavaList(runtimesA map (_.asInstanceOf[Number])), runtimesA map (_.asInstanceOf[Number]))

    val results = ComparisonResults(simA, runtimesA, simB, runtimesB, pValue)

    if (pValue >= alpha) {
      StateUpdate.specify(Seq(AddLiterals(similarLiteral(simA, simB), similarLiteral(simB, simA)), temporaryLiteralFix),
        Map(similarLiteral(simA, simB) -> results, similarLiteral(simB, simA) -> results))
    } else {
      val meanRuntimeA = ArithmeticMean.arithmeticMean(runtimesA.toArray)
      val meanRuntimeB = ArithmeticMean.arithmeticMean(runtimesB.toArray)
      val resultLiteral = if (meanRuntimeA < meanRuntimeB) fasterThanLiteral(simA, simB) else fasterThanLiteral(simB, simA)
      StateUpdate.specify(Seq(AddLiterals(resultLiteral), temporaryLiteralFix), Map(resultLiteral -> results))
    }
  }
}

case class ComparisonResults(
  simA: SingleSimulator,
  resultsA: Seq[Double],
  simB: SingleSimulator,
  resultsB: Seq[Double],
  pValue: Double) extends UserDomainEntity

object CompareSimulatorsSpecification extends ActionSpecification {

  import CollectionHelpers._

  def fasterThanLiteral(simA: SingleSimulator, simB: SingleSimulator) = simA + " faster than " + simB
  def similarLiteral(simA: SingleSimulator, simB: SingleSimulator) = simA + " similar to " + simB

  override def shortName = "Compare Simulators"

  override def description = "Compares simulators with each other."

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]] = {

    val simulators = filterType[SingleSimulator](spec._1)
    val potentialComparisons = allTuples(simulators)

    if (declaredActions(this).nonEmpty || potentialComparisons.isEmpty) {
      None
    } else {
      val actions = for (comparison <- potentialComparisons) yield {
        val fasterAB = PublicLiteral(fasterThanLiteral(comparison._1, comparison._2))
        val fasterBA = PublicLiteral(fasterThanLiteral(comparison._2, comparison._1))
        val similarAB = PublicLiteral(similarLiteral(comparison._1, comparison._2))
        val similarBA = PublicLiteral(similarLiteral(comparison._2, comparison._1))
        SimpleActionDeclaration(this, shortActionName, Seq(), PublicLiteral(calibratedModel), Seq(
          ActionEffect(add = Seq(fasterAB), nondeterministic = true),
          ActionEffect(add = Seq(fasterBA), nondeterministic = true),
          ActionEffect(add = Seq(similarAB, similarBA), nondeterministic = true)),
          actionSpecifics = Some(comparison))
      }
      Some(actions)
    }
  }

  override def createAction(a: ActionDeclaration, c: ExecutionContext) = {
    a match {
      case s: SimpleActionDeclaration =>
        {
          s.actionSpecifics match {
            case Some((s1: SingleSimulator, s2: SingleSimulator)) => CompareSimulators(s1, s2)
            case _ => ???
          }
        }
      case _ => ???
    }
  }

}