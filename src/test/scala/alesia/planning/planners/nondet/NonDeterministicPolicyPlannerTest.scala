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

  /** Checks that actual plan exists and logs it with a description. */
  def checkPlan(plan: Plan, desc: String) = {
    assert(plan != FailurePolicy)
    logPlanRepresentation(desc, plan)
  }

  describe("The non-deterministic planner") {

    it("returns with a failure for the trivial planning problem that does not define any actions ") {
      assert(new NonDeterministicPolicyPlanner().plan(new TrivialPlanningProblem) === FailurePolicy)
      assert(new NonDeterministicPolicyPlanner().createPlan(
        new TrivialPlanningProblem, NonDeterministicPlanTypes.Strong) === FailurePolicy)
    }

    it("returns a correct policy for the trivial planning problem that does define an action") {
      val problem = new TrivialPlanningProblem {
        val solve = action("solve", solvable, Effect(solvable, add = List(solved)))
      }
      val weakPlan = new NonDeterministicPolicyPlanner().plan(problem)
      checkPlan(weakPlan, "Weak plan for trivial planning problem")
      assert(weakPlan.asInstanceOf[DeterministicPolicyPlan].decide(problem.initialState.id) === 0)
      val strongPlan = new NonDeterministicPolicyPlanner().createPlan(problem, NonDeterministicPlanTypes.Strong)
      checkPlan(strongPlan, "Strong plan for trivial planning problem")
      assert(strongPlan.asInstanceOf[DeterministicPolicyPlan].decide(problem.initialState.id) === 0)
    }

    it("is able to deal with non-deterministic problems") {
      val problem = new TrivialPlanningProblem {
        val useActA = v("use-action-a")
        val useActB = v("use-action-b")
        val solveWithA = action("solveWithA", solvable and useActA, Effect(solvable and useActA, add = List(solved)))
        val solveWithB = action("solveWithB", solvable and useActB, Effect(solvable and useActB, add = List(solved)))
        val trySolutions = action("trySolutions", solvable, Effect(solvable, add = List(useActA or useActB)))
      }

      val weakPlan = new NonDeterministicPolicyPlanner().plan(problem)
      checkPlan(weakPlan, "Weak plan for non-deterministic trivial planning problem")
      val strongPlan = new NonDeterministicPolicyPlanner().createPlan(problem, NonDeterministicPlanTypes.Strong)
      checkPlan(strongPlan, "Strong plan for non-deterministic trivial planning problem")
    }

    it("is able to solve sample problem given in 'Automatic OBDD-based Generation of Universal Plans in Non-Deterministic Domains', by Cimatti et al. '98") {
      val weakPlan = new NonDeterministicPolicyPlanner().plan(new SamplePlanningProblemTransport)
      checkPlan(weakPlan, "Weak plan for sample planning problem")
      //TODO: Check if policy is correct
    }

    it("is able to solve strong-cyclic plans") {
      //val strongCyclicPlan = new NonDeterministicPolicyPlanner().createPlan(new SamplePlanningProblemTransport, NonDeterministicPlanTypes.StrongCyclic)
      //TODO: Check if policy is correct
      pending
    }

    it("is able to solve weak plans") {
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