package alesia.planning.actions

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSpec

/**
 * Tests {@link ActionFormula} implementation.
 *
 * @see ActionFormula
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestActionFormula extends FunSpec {

  val (a, b, c) = ("a", "b", "c")

  describe("Formula implementation for action specification") {
    it("correctly resolves public and private literals") {
      val actionFormula = !(!PublicLiteral(a) and (PrivateLiteral(b) or PublicLiteral(c)))
      assert(Seq(PublicLiteral(a), PublicLiteral(c)) === actionFormula.publicLiterals)
      assert(Seq(PrivateLiteral(b)) === actionFormula.privateLiterals)
    }
  }

}