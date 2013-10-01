package alesia.planning.execution

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import alesia.query.UserDomainEntity

/**
 * Tests for [[StateUpdate]].
 *
 * @author Roland Ewald
 */
@RunWith(classOf[JUnitRunner])
class TestStateUpdate extends FunSpec with ShouldMatchers {

  describe("State Update") {

    it("checks for inconsistent changes in literals") {
      val addABC = AddLiterals("a", "b", "c")
      val addX = AddLiterals("x")
      val removeXYA = RemoveLiterals("x", "y", "a")

      StateUpdate(addABC).isConsistent should be(true)
      StateUpdate(addABC, addX).isConsistent should be(true)
      StateUpdate(addABC, addX, removeXYA).isConsistent should be(false)
      StateUpdate(addX, removeXYA).isConsistent should be(false)
      StateUpdate(addABC, removeXYA).isConsistent should be(false)
    }

    it("checks for inconsistent changes in user domain entities") {
      val e1 = new UserDomainEntity {}
      val e2 = new UserDomainEntity {}
      val addE1 = AddEntities(e1)
      val addE2 = AddEntities(e2)
      val removeE1 = RemoveEntities(e1)
      val removeE2 = RemoveEntities(e2)

      StateUpdate(addE1, addE2).isConsistent should be(true)
      StateUpdate(removeE1, addE2).isConsistent should be(true)
      StateUpdate(removeE1, addE1).isConsistent should be(false)
      StateUpdate(addE2, addE1, removeE1).isConsistent should be(false)
      StateUpdate(addE2, addE1, removeE2).isConsistent should be(false)
    }

    it("checks for inconsistent changes in literal<->entity links") {
      val e1 = new UserDomainEntity {}
      val e2 = new UserDomainEntity {}
      StateUpdate.specify(add = Map("a" -> e1, "b" -> e2), del = Map("b" -> e1)).isConsistent should be(true)
      StateUpdate.specify(add = Map("a" -> e1, "b" -> e2), del = Map("b" -> e2)).isConsistent should be(false)
    }
  }

}