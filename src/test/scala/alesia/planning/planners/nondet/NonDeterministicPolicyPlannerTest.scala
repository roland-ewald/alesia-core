package alesia.planning.planners.nondet

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import alesia.planning.PlanningDomain
import alesia.planning.TrivialPlanningProblem
import alesia.planning.plans.Plan
import sessl.util.Logging
import alesia.planning.SamplePlanningProblemTransport
import alesia.planning.TrivialStrongCyclicPlanningProblem
import alesia.planning.TrivialPlanningProblemSolvableDeterministic
import alesia.planning.TrivialPlanningProblemSolvableNonDeterministic

/**
 * Tests for the non-deterministic policy planner.
 *
 * @author Roland Ewald
 *
 */
@RunWith(classOf[JUnitRunner])
class NonDeterministicPolicyPlannerTest extends FunSpec with Logging {

  val numOfTrivialNonDetPlanActions = 5

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
      assert(new NonDeterministicPolicyPlanner().createPlan(
        new TrivialPlanningProblem, NonDeterministicPlanTypes.StrongCyclic) === FailurePolicy)
    }

    it("returns a correct policy for the trivial planning problem that does define an action") {

      val problem = new TrivialPlanningProblemSolvableDeterministic

      val weakPlan = new NonDeterministicPolicyPlanner().plan(problem)
      checkPlan(weakPlan, "Weak plan for trivial planning problem")
      assert(weakPlan.decide(problem.initialState.id).head === 0)

      val strongPlan = new NonDeterministicPolicyPlanner().createPlan(problem, NonDeterministicPlanTypes.Strong)
      checkPlan(strongPlan, "Strong plan for trivial planning problem")
      assert(strongPlan.decide(problem.initialState.id).head === 0)

      val strongCyclicPlan = new NonDeterministicPolicyPlanner().createPlan(problem, NonDeterministicPlanTypes.StrongCyclic)
      assert(strongCyclicPlan.isInstanceOf[DeterministicDistanceBasedPlan])
      assert(strongCyclicPlan.decide(problem.initialState.id).head === 0)
      assert(strongCyclicPlan.decide(problem.initialState.id).toList.length === 1)
    }

    it("is able to deal with non-deterministic problems") {
      val weakPlan = new NonDeterministicPolicyPlanner().plan(new TrivialPlanningProblemSolvableNonDeterministic)
      checkPlan(weakPlan, "Weak plan for non-deterministic trivial planning problem")

      val strongPlan = new NonDeterministicPolicyPlanner().createPlan(new TrivialPlanningProblemSolvableNonDeterministic, NonDeterministicPlanTypes.Strong)
      checkPlan(strongPlan, "Strong plan for non-deterministic trivial planning problem")

      val strongCyclicPlan = new NonDeterministicPolicyPlanner().createPlan(new TrivialPlanningProblemSolvableNonDeterministic, NonDeterministicPlanTypes.StrongCyclic)
      assert(strongCyclicPlan.isInstanceOf[DeterministicDistanceBasedPlan])
    }

    it("is able to find weak plans") {
      val weakPlan = new NonDeterministicPolicyPlanner().plan(new TrivialStrongCyclicPlanningProblem(numOfTrivialNonDetPlanActions))
      checkPlan(weakPlan, "Weak plan for non-deterministic strong-cyclic planning problem")
    }

    it("is able to find strong-cyclic plans") {
      val prob = new TrivialStrongCyclicPlanningProblem(numOfTrivialNonDetPlanActions)
      val strongCyclicPlan = new NonDeterministicPolicyPlanner().createPlan(prob, NonDeterministicPlanTypes.StrongCyclic)
      assert(strongCyclicPlan.isInstanceOf[DeterministicDistanceBasedPlan])
      assert(strongCyclicPlan.decide(prob.goalStates).isEmpty)
      for (i <- 0 until numOfTrivialNonDetPlanActions) {
        val decision = strongCyclicPlan.decide(prob.stepVariables(i).id).toList
        assert(decision.length === 1)
        assert(decision.head == i)
      }
    }

    it("is able to solve sample problem given in 'Automatic OBDD-based Generation of Universal Plans in Non-Deterministic Domains', by Cimatti et al. '98") {
      val weakPlan = new NonDeterministicPolicyPlanner().plan(new SamplePlanningProblemTransport)
      checkPlan(weakPlan, "Weak plan for sample planning problem")

      val strongPlan = new NonDeterministicPolicyPlanner().createPlan(new SamplePlanningProblemTransport, NonDeterministicPlanTypes.Strong)
      assert(strongPlan === FailurePolicy)
    }

    it("is able to find a strong-cyclic plan for sample problem given in 'Automatic OBDD-based Generation of Universal Plans in Non-Deterministic Domains', by Cimatti et al. '98") {
      val strongCyclicPlan = new NonDeterministicPolicyPlanner().createPlan(new SamplePlanningProblemTransport, NonDeterministicPlanTypes.StrongCyclic)
      //      assert(strongCyclicPlan.isInstanceOf[DeterministicDistanceBasedPlan]) //FIXME
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

  }

}