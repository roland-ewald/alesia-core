package alesia.experiments.configuration.frace

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TestFRace extends FunSpec {

  describe("F-Race") {

    it("works for simple problem") {

      import SimpleExecutionEnvironment._

      for (i <- 1 to 100)
        println(measurePerformance((5, Map()), (5, Map())))
      //...
    }
  }

}

