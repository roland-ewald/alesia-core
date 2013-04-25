package alesia.planning.preparation

import org.scalatest.FunSpec
import org.junit.Assert
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Tests {@link DefaultPlanningPreparator}.
 *
 * @see DefaultPlanningPreparator
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestDefaultPlanningPreparator extends FunSpec {

  val preparator = new DefaultPlanningPreparator()

  import alesia.query._

  describe("Default Planning Preparator") {

    it("can extract atomic relations from user hypotheses") {

      val simpleSingleElem = preparator.extractHypothesisElements(exists >> model | hasProperty("qss"))
      Assert.assertEquals(1, simpleSingleElem.length)
      Assert.assertEquals(simpleSingleElem(0)._1, exists)

    }

    it("works for a simple hypothesis") {

      val (problem, context) = preparator.preparePlanning(
        (Seq(SingleModel("java://examples.sr.LinearChainSystem")),
          Seq(WallClockTimeMaximum(seconds = 30)),
          exists >> model | hasProperty("qss")))

      Assert.assertNotNull(context)
      Assert.assertNotNull(problem)
      //TODO: finish this
      pending
    }
  }
}
