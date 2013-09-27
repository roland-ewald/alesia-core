package alesia.planning.execution

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.actions.ActionDeclaration
import alesia.planning.plans.EmptyPlan
import alesia.planning.context.LocalJamesExecutionContext
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Tests for [[DefaultPlanExecutor]].
 *
 * @author Roland Ewald
 *
 */
@RunWith(classOf[JUnitRunner])
class TestDefaultPlanExecutor extends FunSpec with ShouldMatchers {

  describe("The default plan executor") {

    val executor = new DefaultPlanExecutor

    val testProblem = new DomainSpecificPlanningProblem() {
      val declaredActions = Map[Int, ActionDeclaration]()
      val planningActions = Map[Int, DomainAction]()
      val functionByName = Seq("a", "b", "c").map(x => (x, v(x))).toMap
      val goalState = FalseVariable
      val initialState = FalseVariable
    }

    val testPlan = new EmptyPlan {}

    val emptyState = ExecutionState(testProblem, testPlan, LocalJamesExecutionContext())

    it("should work with an emptty update of the execution state context") {
      executor.updateState(emptyState, StateUpdate()) should equal(emptyState)
    }

    it("should calculate a correct update of the execution state context (literals)") {
      val acState = executor.updateState(emptyState, StateUpdate(AddLiterals("a", "c")))
      positiveLiterals(acState.context.planState) should equal(Seq("a", "c"))

      val aState = executor.updateState(acState, StateUpdate(RemoveLiterals("b", "c")))
      positiveLiterals(aState.context.planState) should equal(Seq("a"))
    }

    //TODO: Remove =/= negative literal? 2x remove == negative literal?

    def positiveLiterals(ps: PlanState) = ps flatMap { x => if (x._2) Some(x._1) else None }

  }

}