package alesia.planning.planners.nondet

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import alesia.planning.PlanningDomain
import alesia.planning.PlanningProblem
import alesia.planning.SamplePlanningProblemTransport
import alesia.planning.TrivialPlanningProblem
import sessl.util.Logging

/**
 * Tests for the non-deterministic policy planner.
 *
 * @author Roland Ewald
 *
 */
@RunWith(classOf[JUnitRunner])
class NonDeterministicPolicyPlannerTest extends FunSpec with Logging {

  describe("The OBDD-Planner") {

    it("returns with a failure for the trivial planning problem that does not define any actions ") {
      assert(new NonDeterministicPolicyPlanner().plan(new TrivialPlanningProblem) === FailurePolicy)
    }

    it("returns a correct policy for the trivial planning problem that does define an action") {
      val problem = new TrivialPlanningProblem {
        val solve = action("solve", solvable, Effect(solvable, add = List(solved)))
      }
      val plan = new NonDeterministicPolicyPlanner().plan(problem)
      assert(plan != FailurePolicy)
      logger.info("Plan for trivial planning problem:" + plan)
      assert(plan.asInstanceOf[DeterministicPolicy].decide(problem.initialState.id) === 0)
    }

    it("is able to solve sample problem given in 'Automatic OBDD-based Generation of Universal Plans in Non-Deterministic Domains', by Cimatti et al. '98") {
      val plan = new NonDeterministicPolicyPlanner().plan(new SamplePlanningProblemTransport)
      logger.info("Plan for sample planning problem:" + plan)
      assert(plan != FailurePolicy)
      //TODO: Pretty-print plans and implement execution control / plan validation
    }

    it("is able to solve a simple problem in the ALeSiA domain") {
      pending
    }

    it("terminates when confronted with a planning problem that does not have a solution") {
      pending
    }

    it("warns the user when the planning problem is strangely defined (no actions etc.)") {
      pending
    }

    it("warns the user and stops execution when the state space grows too large") {
      pending
    }
  }

}