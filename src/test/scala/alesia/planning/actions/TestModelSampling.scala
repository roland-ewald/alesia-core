package alesia.planning.actions

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

import alesia.planning.actions.housekeeping.ModelSamplingSpecification
import alesia.query._

/**
 * Tests for [[alesia.planning.actions.housekeeping.ModelSampling]].
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestModelSampling extends FunSpec with ShouldMatchers {

  describe("Model sampling") {

    val declaredActions = ModelSamplingSpecification.declareConcreteActions(
      (Seq(
        ModelSet("model:/A"),
        ModelSet("model:/b", ModelParameter("test", 1, 1, 10))), Seq(), DummyHypothesis),
      Map(ModelSamplingSpecification -> Seq()))

    it("gets declared as an action for each individual model set") {
      pending
    }

    it("works in principle") {
      pending
    }
  }

}