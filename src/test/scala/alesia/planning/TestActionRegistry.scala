package alesia.planning

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import alesia.planning.actions.Action
import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.ActionRegistry
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.AllDeclaredActions
import alesia.planning.actions.PublicLiteral
import alesia.planning.actions.TrueFormula
import alesia.planning.context.ExecutionContext
import alesia.query.ProblemSpecification
import org.scalatest.junit.JUnitRunner
import alesia.planning.execution.NoStateUpdate

/**
 * Tests for ActionRegistry. The test is not situated in <code>alessia.planning.actions</code> because it should
 * also test to configure the registry with *other* package names to load application specifications from.
 *
 * @see ActionRegistry
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestActionRegistry extends FunSpec {

  describe("The action registry") {

    it("loads action specifications per default") {
      assert(ActionRegistry.actionSpecifications.length > 0)
    }

    it("can be configured via the property '" + ActionRegistry.propertyToAddCustomPath + "'") {
      System.setProperty(ActionRegistry.propertyToAddCustomPath, getClass().getPackage().getName())
      ActionRegistry.rescanActionSpecifications()
      assert(ActionRegistry.actionSpecifications.exists(_.eq(TestActionSpecification)))
    }
  }

}

/** Dummy action for testing. */
class DummyAction extends Action {

  override def execute(e: ExecutionContext) = NoStateUpdate

}

/** Dummy action specification for testing. */
object TestActionSpecification extends ActionSpecification {

  override def preCondition: ActionFormula = TrueFormula

  override def effect: ActionFormula = PublicLiteral("")

  override def createAction(a: ActionDeclaration, c: ExecutionContext) = new DummyAction

  override def shortName = "Dummy Action (for testing)"

  override def description = "If you see this in production, remove the test-jars from the classpath."

  override def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]] = None
}