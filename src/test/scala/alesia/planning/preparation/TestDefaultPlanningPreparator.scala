package alesia.planning.preparation

import org.scalatest.FunSpec
import org.junit.Assert
import org.junit.Assert._
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

  import alesia.query._

  val preparator = new DefaultPlanningPreparator()

  val simpleUserSpecification = (Seq(
    SingleModel("java://examples.sr.LinearChainSystem"),
    SingleModel("java://examples.sr.TotallyIndependentSystem")),
    Seq(WallClockTimeMaximum(seconds = 30)),
    exists >> model | hasProperty("qss"))

  describe("Default Planning Preparator") {

    it("can extract atomic relations from user hypotheses") {

      def assertProperty(name: String, e: (Quantifier, PredicateSubject, PredicateRelation)) =
        assertEquals(hasProperty(name), e._3)

      val simpleSingleElem = preparator.extractHypothesisElements(exists >> model | hasProperty("qss"))
      assertEquals(1, simpleSingleElem.length)
      assertProperty("qss", simpleSingleElem(0))

      val multipleElems = preparator.extractHypothesisElements(exists >> model | ((hasProperty("qss") and hasProperty("small")) or hasProperty("nested")))
      assertEquals(3, multipleElems.length)
      assertProperty("qss", multipleElems(0))
      assertProperty("small", multipleElems(1))
      assertProperty("nested", multipleElems(2))
    }

    it("can extract action declarations from action specifications") {
      val declaredActions = DefaultPlanningPreparator.retrieveDeclaredActions(simpleUserSpecification)
      assert(declaredActions.size > 0)
      assert(declaredActions.head._2.size > 0) 
    }

    it("works for a simple hypothesis") {

      val (problem, context) = preparator.preparePlanning(simpleUserSpecification)

      Assert.assertNotNull(context)
      Assert.assertNotNull(problem)
      //TODO: finish this
      pending
    }
  }
}
