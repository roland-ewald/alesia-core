package alesia.planning.planners.nondet

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import alesia.planning.PlanningDomain
import alesia.planning.TrivialPlanningProblem
import alesia.planning.planners.Plan
import sessl.util.Logging
import alesia.planning.SamplePlanningProblemTransport
import alesia.planning.TrivialStrongCyclicPlanningProblem
import alesia.planning.TrivialPlanningProblemSolvableDeterministic
import alesia.planning.TrivialPlanningProblemSolvableNonDeterministic
import NonDeterministicPlanTypes._

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
    logger.info(desc + ":\n" + plan.asInstanceOf[PolicyPlan].symbolicRepresentation)

  /** Checks that actual plan exists and logs it with a description. */
  def checkPlan(plan: Option[Plan], desc: String): Unit = {
    assert(plan.isDefined)
    assert(plan.get != FailurePolicy)
    logPlanRepresentation(desc, plan.get)
  }

  def planner = new NonDeterministicPolicyPlanner()

  describe("The non-deterministic planner") {

    it("returns with a failure for the trivial planning problem that does not define any actions ") {
      assert(new NonDeterministicPolicyPlanner().plan(new TrivialPlanningProblem) === FailurePolicy)
      assert(new NonDeterministicPolicyPlanner().createPlan(
        new TrivialPlanningProblem, Strong) === None)
      assert(new NonDeterministicPolicyPlanner().createPlan(
        new TrivialPlanningProblem, StrongCyclic) === None)
    }

    it("returns a correct policy for the trivial planning problem that does define an action") {

      val problem = new TrivialPlanningProblemSolvableDeterministic

      val weakPlan = planner.createPlan(problem, Weak)
      checkPlan(weakPlan, "Weak plan for trivial planning problem")
      assert(weakPlan.get.decide(problem.initialState.id).head === 0)

      val strongPlan = planner.createPlan(problem, Strong)
      checkPlan(strongPlan, "Strong plan for trivial planning problem")
      assert(strongPlan.get.decide(problem.initialState.id).head === 0)

      val strongCyclicPlan = planner.createPlan(problem, StrongCyclic)
      assert(strongCyclicPlan.get.isInstanceOf[DistanceBasedPlan])
      assert(strongCyclicPlan.get.decide(problem.initialState.id).head === 0)
      assert(strongCyclicPlan.get.decide(problem.initialState.id).toList.length === 1)
    }

    it("is able to deal with non-deterministic problems") {
      val weakPlan = planner.createPlan(new TrivialPlanningProblemSolvableNonDeterministic, Weak)
      checkPlan(weakPlan, "Weak plan for non-deterministic trivial planning problem")

      val strongPlan = planner.createPlan(new TrivialPlanningProblemSolvableNonDeterministic, Strong)
      checkPlan(strongPlan, "Strong plan for non-deterministic trivial planning problem")

      val strongCyclicPlan = planner.createPlan(new TrivialPlanningProblemSolvableNonDeterministic, StrongCyclic)
      assert(strongCyclicPlan.get.isInstanceOf[DistanceBasedPlan])
    }

    it("is able to find weak plans") {
      val weakPlan = planner.createPlan(new TrivialStrongCyclicPlanningProblem(numOfTrivialNonDetPlanActions), Weak)
      checkPlan(weakPlan, "Weak plan for non-deterministic strong-cyclic planning problem")
    }

    it("is able to find strong-cyclic plans") {
      val prob = new TrivialStrongCyclicPlanningProblem(numOfTrivialNonDetPlanActions)
      val strongCyclicPlan = planner.createPlan(prob, StrongCyclic)
      assert(strongCyclicPlan.isDefined)
      assert(strongCyclicPlan.get.decide(prob.goalStates).isEmpty)
      for (i <- 0 until numOfTrivialNonDetPlanActions) {
        val decision = strongCyclicPlan.get.decide(prob.stepVariables(i).id).toList
        assert(decision.length === 1)
        assert(decision.head == i)
      }
    }

    it("is able to fail-over, i.e. tries to find a weak plan in case no strong or strong-cyclic plan can be found") {
      val weakPlan = planner.createPlan(new TrivialStrongCyclicPlanningProblem(numOfTrivialNonDetPlanActions), Weak)
      val betterPlan = planner.plan(new TrivialStrongCyclicPlanningProblem(numOfTrivialNonDetPlanActions))
      assert(weakPlan.getClass != betterPlan.getClass)
    }

    it("is able to find a weak plan for sample problem in paper by Cimatti et al. '98") {
      val weakPlan = planner.createPlan(new SamplePlanningProblemTransport, Weak)
      checkPlan(weakPlan, "Weak plan for sample planning problem")
    }

    it("is able to find a strong-cyclic plan for sample problem in paper by Cimatti et al. '98") {
      val strongCyclicPlan = planner.createPlan(new SamplePlanningProblemTransport, StrongCyclic)
      //      assert(strongCyclicPlan.isDefined) //FIXME
      pending
    }

    it("is not able to find a strong plan for sample problem in paper by Cimatti et al. '98") {
      val strongPlan = planner.createPlan(new SamplePlanningProblemTransport, Strong)
      assert(!strongPlan.isDefined)
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