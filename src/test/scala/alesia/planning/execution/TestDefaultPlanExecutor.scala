package alesia.planning.execution

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.actions.ActionDeclaration
import alesia.planning.plans.EmptyPlan
import alesia.planning.context.LocalJamesExecutionContext
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import alesia.query.SingleModel
import alesia.planning.domain.ParameterizedModel

/**
 * Tests for [[DefaultPlanExecutor]].
 *
 * @author Roland Ewald
 *
 */
@RunWith(classOf[JUnitRunner])
class TestDefaultPlanExecutor extends FunSpec with ShouldMatchers {

  describe("The default plan executor") {

    import DefaultPlanExecutor._

    val executor = new DefaultPlanExecutor

    val testSelector = RandomActionSelector

    val testProblem = new DomainSpecificPlanningProblem() {
      val declaredActions = Map[Int, ActionDeclaration]()
      val planningActions = Map[Int, DomainAction]()
      val functionByName = Seq("a", "b", "c").map(x => (x, v(x))).toMap
      val goalState = FalseVariable
      val initialState = FalseVariable
    }

    val testPlan = new EmptyPlan {}

    val testEntities = Seq(SingleModel("x"), SingleModel("y"))

    val emptyState = ExecutionState(testProblem, testPlan, LocalJamesExecutionContext(actionSelector = testSelector))

    //TODO: Removing a literal =/= adding a negative literal! 2x remove == negative literal?

    it("works with an empty update of the execution state context") {
      updateState(emptyState, StateUpdate(), testSelector) should equal(emptyState)
    }

    it("correctly updates the planning domain literals") {
      val acState = updateState(emptyState, StateUpdate(AddLiterals("a", "c")), testSelector)
      positiveLiterals(acState.context.planState) should equal(Seq("a", "c"))

      val aState = updateState(acState, StateUpdate(RemoveLiterals("b", "c")), testSelector)
      positiveLiterals(aState.context.planState) should equal(Seq("a"))
    }

    it("correctly updates user domain entities") {
      val entitiesState = updateState(emptyState, StateUpdate(AddEntities(testEntities: _*)), testSelector)
      entitiesState.context.entities should equal(testEntities)
      entitiesState.context.entitiesOf[SingleModel] should equal(testEntities)
      entitiesState.context.entitiesOf[ParameterizedModel] should equal(Seq.empty)

      val entityState = updateState(entitiesState, StateUpdate(RemoveEntities(testEntities.last)), testSelector)
      entityState.context.entities should equal(Seq(testEntities.head))
    }

    it("correctly updates the links between literals and entities") {
      val aElem = "a" -> testEntities.head
      val a2Elem = "a" -> testEntities.last
      val bElem = "b" -> testEntities.last

      val newState = updateState(emptyState, StateUpdate.specify(add = Map(aElem, bElem)), testSelector)
      links(newState).size should be(2)
      links(newState)(aElem._1).size should be(1)
      links(newState)(aElem._1).head should be(aElem._2)
      links(newState)(bElem._1).size should be(1)
      links(newState)(bElem._1).head should be(bElem._2)

      val twoAState = updateState(newState, StateUpdate.specify(add = Map(aElem._1 -> bElem._2, bElem)), testSelector)
      links(twoAState).size should be(2)
      links(twoAState)(aElem._1).size should be(2)
      links(twoAState)(aElem._1).head should be(aElem._2)
      links(twoAState)(aElem._1).last should be(bElem._2)
      links(twoAState)(bElem._1).size should be(1) //because only distinct elements are stored

      val oneAState = updateState(twoAState, StateUpdate.specify(del = Map(aElem)), testSelector)
      links(oneAState).size should be(2)
      links(oneAState)(aElem._1).size should be(1)
      links(oneAState)(aElem._1).head should be(bElem._2)
    }

    def positiveLiterals(ps: PlanState) = ps flatMap { x => if (x._2) Some(x._1) else None }

    def links(e: ExecutionState) = e.context.entitiesForLiterals

  }

}