package alesia.planning

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

/**
 * Tests functioning of both planning domain and planning problem, with a sample problem definiton.
 *
 * @see PlanningDomain
 * @see PlanningProblem
 *
 * @author Roland Ewald
 *
 */
@RunWith(classOf[JUnitRunner])
class TestPlanningProblemRepresentation extends FunSuite {

  val sampleProblem = new SamplePlanningProblemTransport

  test("number of actions is correct") {
    assert(sampleProblem.numActions === 7)
  }

  test("there are several variables and functions") {
    assert(sampleProblem.numVariables > 10)
    assert(sampleProblem.numFunctions > 20)
  }

}