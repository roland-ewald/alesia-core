package alesia.planning.actions

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import alesia.planning.actions.housekeeping.ModelSamplingSpecification
import alesia.query._
import alesia.planning.actions.housekeeping.SamplingData

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

  describe("Model sampling") {

    lazy val declaredActions = ModelSamplingSpecification.declareConcreteActions(
      (testModelSets, Seq(), DummyHypothesis), Map(ModelSamplingSpecification -> Seq()))

    lazy val simpleDeclarations = declaredActions.get collect { case x: SimpleActionDeclaration => x }

    lazy val samplingData = simpleDeclarations flatMap (_.actionSpecifics) collect { case s: SamplingData => s }

    it("gets declared as an action for each individual model set") {
      declaredActions.isDefined should be(true)
      declaredActions.get.size should equal(testModelSets.size)
    }

    it("has a spcification that correctly initializes the initial sampling state") {
      simpleDeclarations.size should equal(testModelSets.size)
      simpleDeclarations foreach { _.actionSpecifics.isDefined should be(true) }
      samplingData.size should equal(testModelSets.size)
      samplingData(0).modelSet.setURI should not equal (samplingData(1).modelSet.setURI)
    }

    it("works in principle") {
      pending
    }
  }

}