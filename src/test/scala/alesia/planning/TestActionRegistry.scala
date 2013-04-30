package alesia.planning

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import alesia.planning.actions.ActionRegistry
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.Action
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.PublicLiteral
import alesia.planning.context.ExecutionContext

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
      System.setProperty(ActionRegistry.propertyToAddCustomPath, "alesia.plannning")
      ActionRegistry.rescanActionSpecifications()
      assert(ActionRegistry.actionSpecifications.exists(_.eq(TestActionSpecification)))
      println("Done")
    }
  }

}

/** Dummy action for testing. */
class DummyAction extends Action[Any] {

  override def execute(implicit provider: Any) = {}

  override def resultFor(key: String): Option[AnyRef] = None
}

/** Dummy action specification for testing. */
object TestActionSpecification extends ActionSpecification[Any, DummyAction] {

  override def preCondition: Option[ActionFormula] = None

  override def effect: ActionFormula = PublicLiteral("")

  override def publicLiterals = Seq()

  override def privateLiterals = Seq()

  override def createAction(logicalName: String, c: ExecutionContext) = new DummyAction

  override def shortName = "Dummy Action (for testing)"

  override def description = "If you see this in production, remove the test-jars from the classpath."
}