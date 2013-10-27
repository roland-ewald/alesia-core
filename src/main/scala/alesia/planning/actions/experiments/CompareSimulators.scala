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

/**
 * Compare two (calibrated) simulators with each other.
 *
 * @author Roland Ewald
 */
case class CompareSimulators(simA: SingleSimulator, simB: SingleSimulator) extends ExperimentAction {

  override def execute(e: ExecutionContext) = {

    StateUpdate.specify(Seq(AddLiterals(calibratedModel)), Map())
  }

}

object CompareSimulatorsSpecification extends ActionSpecification {

  import CollectionHelpers._

  override def shortName = "Compare Simulators"

  override def description = "Compares simulators with each other."

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]] = {

    val simulators = filterType[SingleSimulator](spec._1)
    val potentialComparisons = allTuples(simulators)

    if (declaredActions(this).nonEmpty || potentialComparisons.isEmpty) {
      None
    } else {
      val actions = for (comparison <- potentialComparisons) yield {
        val fasterAB = PublicLiteral(comparison._1 + " faster than " + comparison._2)
        val fasterBA = PublicLiteral(comparison._2 + " faster than " + comparison._1)
        val similar = PublicLiteral(comparison._2 + " similar to " + comparison._1)
        SimpleActionDeclaration(this, shortActionName, Seq(), PublicLiteral(calibratedModel), Seq(
          ActionEffect(add = Seq(fasterAB), nondeterministic = true),
          ActionEffect(add = Seq(fasterBA), nondeterministic = true),
          ActionEffect(add = Seq(similar), nondeterministic = true)),
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