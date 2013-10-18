package alesia.planning.execution

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.DummyDomainSpecificPlanningProblem
import alesia.planning.actions.ActionDeclaration
import alesia.planning.context.ExecutionContext
import alesia.planning.context.ExecutionContext
import alesia.planning.context.LocalJamesExecutionContext
import alesia.planning.planners.nondet.EmptyPolicy
import alesia.planning.planners.EmptyPlan
import alesia.planning.planners.EmptyPlan
import alesia.planning.planners.EmptyPlan
import alesia.planning.planners.EmptyPlan
import alesia.planning.planners.EmptyPlan
import alesia.planning.planners.EmptyPlan

/**
 * Tests the trivial implementations of [[alesia.planning.execution.ActionSelector]].
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestActionSelectors extends FunSpec with ShouldMatchers {

  object TestState extends ExecutionState(DummyDomainSpecificPlanningProblem(), EmptyPolicy, LocalJamesExecutionContext())

  val testActions = IndexedSeq(Seq(1, 2, 3), Seq(3, 23, 435))

  /** This should hold for all [[alesia.planning.execution.ActionSelector]] implementations. */
  def shouldFailCorrectly(as: ActionSelector) = {
    it("should fail correctly for empty action lists") {
      evaluating {
        as(Seq.empty, TestState) should be(1)
      } should produce[Exception]
    }
  }

  describe("First action selector") {
    it("should select always the first action") {
      FirstActionSelector(testActions(0), TestState)._1 should be(testActions(0).head)
      FirstActionSelector(testActions(1), TestState)._1 should be(testActions(1).head)
    }
    shouldFailCorrectly(FirstActionSelector)
  }

  describe("Min action index selector") {
    it("should always select action with minimum index") {
      MinActionIndexSelector(testActions(0), TestState)._1 should be(testActions(0).min)
      MinActionIndexSelector(testActions(1), TestState)._1 should be(testActions(1).min)
    }
    shouldFailCorrectly(MinActionIndexSelector)
  }

  describe("Patient action selector") {

    val impatientSelector = PatientActionSelector(1)
    val patientChoice = MinActionIndexSelector(testActions(0), TestState)._1

    it("should switch to another action immediately, if impatient") {
      val res1 = impatientSelector(testActions(0), TestState)
      res1._1 should be(patientChoice)
      res1._2(testActions(0), TestState) should not be (patientChoice)
    }

    it("should be more patient if configured accordingly") {
      val res1 = PatientActionSelector(3)(testActions(0), TestState)
      res1._1 should be(patientChoice)

      val res2 = res1._2(testActions(0), TestState)
      res2._1 should be(patientChoice)

      val res3 = res2._2(testActions(0), TestState)
      res3._1 should be(patientChoice)

      res3._2(testActions(0), TestState)._1 should not be (patientChoice)
    }

    shouldFailCorrectly(impatientSelector)
  }

}