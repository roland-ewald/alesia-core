package alesia.planning.planners.nondet

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import alesia.planning.PlanningDomain
import alesia.planning.PlanningProblem
import alesia.planning.SamplePlanningProblemTransport
import alesia.planning.TrivialPlanningProblem
import sessl.util.Logging
import alesia.planning.plans.Plan

/**
 * Tests for the non-deterministic policy planner.
 *
 * @author Roland Ewald
 *
 */
@RunWith(classOf[JUnitRunner])
class NonDeterministicPolicyPlannerTest extends FunSpec with Logging {

  /** Logs plan representation. */
  def logPlanRepresentation(desc: String, plan: Plan) =
    logger.info(desc + ":\n" + plan.asInstanceOf[DeterministicPolicyPlan].symbolicRepresentation)

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
      logPlanRepresentation("Plan for trivial planning problem", plan)
      assert(plan.asInstanceOf[DeterministicPolicyPlan].decide(problem.initialState.id) === 0)
    }

    it("is able to solve sample problem given in 'Automatic OBDD-based Generation of Universal Plans in Non-Deterministic Domains', by Cimatti et al. '98") {
      val plan = new NonDeterministicPolicyPlanner().plan(new SamplePlanningProblemTransport)
      assert(plan != FailurePolicy)
      logPlanRepresentation("Plan for sample planning problem", plan)
      //TODO: Check if policy is correct
    }

    it("is able to deal with non-deterministic problems") {
      val plan = new NonDeterministicPolicyPlanner() plan {
        new TrivialPlanningProblem {
          val useActA = v("use-action-a")
          val useActB = v("use-action-b")
          val solveWithA = action("solveWithA", solvable and useActA, Effect(solvable and useActA, add = List(solved)))
          val solveWithB = action("solveWithB", solvable and useActB, Effect(solvable and useActB, add = List(solved)))
          val trySolutions = action("trySolutions", solvable, Effect(solvable, add = List(useActA or useActB)))
        }
      }
      assert(plan != FailurePolicy)
      logPlanRepresentation("Plan for trivial non-deterministic planning problem", plan)
      //TODO: Check if policy is correct
    }

    it("is able to solve generate strong-cyclic plans") {
      pending
    }

    it("is able to solve generate weak plans") {
      pending
    }

    it("produces deterministic policies that can be executed and will work") {
      pending
    }

    it("allows to consider extended goals") {
      pending
    }

    it("is able to solve a simple problem in the ALeSiA domain") {
      pending
    }

    it("warns the user when the planning problem is strangely defined (no actions etc.)") {
      pending
    }

    it("warns the user and stops execution when the state space grows too large") {
      pending
    }

    //TODO: execution control / plan validation ?
  }

}