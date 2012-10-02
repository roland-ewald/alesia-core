package alesia.planning.planners.nondet

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import alesia.planning.PlanningDomain
import alesia.planning.PlanningProblem
import alesia.planning.SamplePlanningProblemTransport

/**
 * Tests for the non-deterministic policy planner.
 *
 * @author Roland Ewald
 *
 */
@RunWith(classOf[JUnitRunner])
class NonDeterministicPolicyPlannerTest extends FunSpec {

  describe("The OBDD-Planner") {

    it("is able to solve sample problem given in 'Automatic OBDD-based Generation of Universal Plans in Non-Deterministic Domains', by Cimatti et al. '98") {
      val domain = new SamplePlanningProblemTransport
      //TODO
      pending
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