package alesia.planning.scenarios

import org.scalatest.FunSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Tests for a simple scenario that aims to compare the performance of two
 * simulation algorithms on some calibrated benchmark model.
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class SimpleComparison extends FunSpec {

  describe("Simple Comparison Scenario") {

    it("works in principle :)") {

      import alesia.query._
      val results = submit(null, null, null)
      pending
    }

    it("fails whenever elements of the problem specification are missing") {
      pending
    }

    it("can benefit from previously achieved results") {
      pending
    }

    it("can be executed remotely") {
      pending
    }
  }
}

/**
 * Contains all user-defined elements for this scenario.
 */
object SimpleComparison {

  //TODO: Implement & test necessary actions
  //TODO: Specify planning domain (indirectly?)
  //TODO: Specify hypothesis
  //TODO: Implement simple mechanism to transform hypothesis into goal
  //TODO: Implement prototype of execution infrastructure / plan monitor

}