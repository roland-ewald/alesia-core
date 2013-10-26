package alesia.planning.actions

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import alesia.planning.actions.housekeeping.ModelSamplingSpecification
import alesia.query._
import alesia.planning.actions.housekeeping.SamplingData
import alesia.planning.context.LocalJamesExecutionContext
import alesia.planning.actions.housekeeping.ModelSampling
import alesia.planning.execution.StateUpdate

/**
 * Tests for [[alesia.planning.actions.housekeeping.ModelSampling]].
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestModelSampling extends FunSpec with ShouldMatchers {

  val testModelSets = Seq(
    ModelSet("model:/A"),
    ModelSet("model:/B", ModelParameter("test", 1, 1, 10)))

  val context = LocalJamesExecutionContext()

  describe("Model sampling") {

    lazy val declaredActions = ModelSamplingSpecification.declareConcreteActions(
      (testModelSets, Seq(), DummyHypothesis), Map(ModelSamplingSpecification -> Seq()))

    lazy val simpleDeclarations = declaredActions.get collect { case x: SimpleActionDeclaration => x }

    lazy val samplingData = simpleDeclarations flatMap (_.actionSpecifics) collect { case s: SamplingData => s }

    it("gets declared as an action for each individual model set") {
      declaredActions.isDefined should be(true)
      declaredActions.get.size should equal(testModelSets.size)
    }

    it("has a specification that correctly initializes the initial sampling state") {
      simpleDeclarations.size should equal(testModelSets.size)
      simpleDeclarations foreach { _.actionSpecifics.isDefined should be(true) }
      samplingData.size should equal(testModelSets.size)
      samplingData(0).modelSet.setURI should not equal (samplingData(1).modelSet.setURI)
    }

    it("does work only once without parameters") {
      val action = declaredActions.get(0).toExecutableAction(context)
      action.isInstanceOf[ModelSampling] should be(true)
      val update1 = action.execute(context)
      firstAddition(update1).contains("depleted") should be(false)
      val update2 = action.execute(context)
      firstAddition(update2).contains("depleted") should be(true)
    }

    it("works multiple times, and then gets depleted if sample is exhausted") {
      val action = declaredActions.get(1).toExecutableAction(context)
      action.isInstanceOf[ModelSampling] should be(true)
      for (trial <- 1 to 10) {
        val currentUpdate = action.execute(context)
        println(trial)
        firstAddition(currentUpdate).contains("depleted") should be(false)
      }
      val depletedUpdate = action.execute(context)
      firstAddition(depletedUpdate).contains("depleted") should be(true)
    }

    it("works for parameters with various types") {
      pending
    }

    def firstAddition(s: StateUpdate) = s.changes.head.literals.head

  }

}