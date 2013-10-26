package alesia.planning.actions.experiments

import alesia.query.MaxSingleExecutionWallClockTime
import alesia.planning.actions.ActionDeclaration
import alesia.query.ProblemSpecification
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.PublicLiteral
import alesia.planning.actions.AllDeclaredActions
import alesia.utils.misc.CollectionHelpers
import alesia.planning.context.ExecutionContext
import alesia.planning.domain.ParameterizedModel
import alesia.planning.actions.SimpleActionDeclaration
import alesia.query.SingleSimulator
import alesia.planning.actions.SharedLiterals._
import alesia.planning.actions.ActionEffect
import alesia.planning.execution.StateUpdate
import alesia.planning.execution.AddLiterals

/**
 * Compare two (calibrated) simulators with each other.
 *
 * @author Roland Ewald
 */
case class CompareSimulators extends ExperimentAction {

  override def execute(e: ExecutionContext) = {

    StateUpdate.specify(Seq(AddLiterals(calibratedModel)), Map())
  }

}

object CompareSimulatorsSpecification extends ActionSpecification {

  override def shortName = "Compare Simulators"

  override def description = "Calibrates a model can be calibrated."

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]] = {
    singleAction(declaredActions) {
      ???
      //      SimpleActionDeclaration(this, shortActionName, Seq(), PublicLiteral(loadedModel), Seq(
      //        ActionEffect(add = Seq(calibrated), nondeterministic = false)))
    }
  }

  override def createAction(a: ActionDeclaration, c: ExecutionContext) = {

    import CollectionHelpers._

    CompareSimulators()
  }

}