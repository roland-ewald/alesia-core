package alesia.planning

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import alesia.utils.bdd.TestUniqueTable
import alesia.utils.bdd.UniqueTable

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

  test("there are several functions defined") {
    assert(sampleProblem.numFunctions > 20)
  }

  test("all action preconditions have a valid bdd representations") {
    sampleProblem.actions.foreach {
      a => assert(UniqueTable.bddIsValid(a.precondition.id, sampleProblem.table))
    }
  }

}