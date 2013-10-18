package alesia.planning.preparation

import org.scalatest.FunSpec
import org.junit.Assert
import org.junit.Assert._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import alesia.planning.execution.RandomActionSelector
import alesia.planning.execution.ExecutionState
import alesia.planning.execution.ActionSelector
import alesia.planning.execution.WallClockTimeMaximum

/**
 * Tests {@link DefaultPlanningPreparator}.
 *
 * @see DefaultPlanningPreparator
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestDefaultPlanningPreparator extends FunSpec with ShouldMatchers {

  import alesia.query._
  import alesia.TestUtils._

  val preparator = new DefaultPlanningPreparator()

  val testSpec = (Seq(
    SingleModel("java://examples.sr.LinearChainSystem"),
    SingleModel("java://examples.sr.TotallyIndependentSystem")),
    Seq(TerminateWhen(WallClockTimeMaximum(seconds = 30))),
    exists >> model | hasProperty("qss"))

  describe("Default Planning Preparator") {

    it("can extract atomic relations from user hypotheses") {

      def assertProperty(name: String, e: (Quantifier, PredicateSubject, PredicateRelation)) =
        assertEquals(hasProperty(name), e._3)

      val simpleSingleElem = FormulaConverter.extractHypothesisElements(exists >> model | hasProperty("qss"))
      assertEquals(1, simpleSingleElem.length)
      assertProperty("qss", simpleSingleElem(0))

      val multipleElems = FormulaConverter.extractHypothesisElements(exists >> model | ((hasProperty("qss") and hasProperty("small")) or hasProperty("nested")))
      assertEquals(3, multipleElems.length)
      assertProperty("qss", multipleElems(0))
      assertProperty("small", multipleElems(1))
      assertProperty("nested", multipleElems(2))
    }

    it("can extract action declarations from action specifications") {
      val declaredActions = DefaultPlanningPreparator.retrieveDeclaredActions(testSpec)
      assert(declaredActions.size > 0)
      assert(declaredActions.head._2.size > 0)
    }

    it("works for a simple hypothesis") {
      val (problem, context) = preparator.preparePlanning(testSpec)
      Assert.assertNotNull(context)
      Assert.assertNotNull(problem)
    }

    it("uses a default action selector if none is selected") {
      DefaultPlanningPreparator.initializeActionSelector(testSpec) should not be (null)
    }

    it("can be configured with a user-defined action selector") {
      val customSelector = new ActionSelector() {
        override def apply(actionIndices: Iterable[Int], state: ExecutionState) = ???
      }
      val result = DefaultPlanningPreparator.initializeActionSelector((testSpec._1, Seq(StartWithActionSelector(customSelector)), testSpec._3))
      result should be(customSelector)
    }
  }
}
