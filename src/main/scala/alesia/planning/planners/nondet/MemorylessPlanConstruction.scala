package alesia.planning.planners.nondet

import alesia.utils.bdd.UniqueTable
import alesia.planning.planners.Plan
import alesia.planning.PlanningProblem
import alesia.planning.planners.EmptyPlan
import alesia.planning.PlanningDomainAction

/**
 * UNFINISHED implementation of Rintanen's algorithm for plan construction, see his script fig. 4.1, 4.3, 4.4.
 *
 * @author Roland Ewald
 */
object MemorylessPlanConstruction {

  /**
   * Constructs a non-deterministic plan.
   * @param p the planning problem
   * @param D the state sets of distance i to the goal, i.e. D(0) is the set of goal states
   */
  def constructPlan(p: PlanningProblem, D: Array[Int])(implicit t: UniqueTable): Plan = {
    import t._

    val G = p.goalStates
    var count = 1
    var N = 0
    var l = List[Int]()

    /** Construct actual plan. See fig. 4.3. */
    def constrPlan(n: Int, S: Int): Unit = {
      if (isContained(S, G))
        return
      for (o <- p.actions) {
        //How to construct maximal S'? Intersect with the D_i ?
        // TODO: to be continued
      }
    }

    /** An operator op is able to progress a plan if it takes *all* states in S closer to the goal. See fig. 4.4. */
    def progress(op: PlanningDomainAction, S: Int): Boolean = {
      for (i <- 1 until D.length)
        if (!isContained(image(op, intersection(S, D(i))), D(i - 1)))
          return false
      true
    }

    constrPlan(0, p.initialStates)
    throw new UnsupportedOperationException // TODO
  }

  def image(op: PlanningDomainAction, S: Int) = throw new UnsupportedOperationException // TODO

}