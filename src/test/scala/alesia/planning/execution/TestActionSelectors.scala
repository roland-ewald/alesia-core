package alesia.planning.execution

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec

/**
 * Tests the trivial implementations of [[alesia.planning.execution.ActionSelector]].
 *
 * @author roland
 */
@RunWith(classOf[JUnitRunner])
class TestActionSelectors extends FunSpec with ShouldMatchers {

  describe("Deterministic action selector") {
    it("should select always the same action") {
      pending
    }
  }

}