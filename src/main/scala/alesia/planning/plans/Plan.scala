package alesia.planning.plans

import alesia.planning.actions.Action
import alesia.planning.actions.experiments.ExperimentAction
import alesia.planning.context.ExecutionContext

/**
 * Interface for a plan.
 *
 *  @author Roland Ewald
 */
trait Plan {

  /**
   * Decide upon action(s) based on the current state.
   * @return indices of actions that could be tried
   */
  def decide(state: Int): Iterable[Int] = throw new UnsupportedOperationException
}

/** Trivial plan. */
trait EmptyPlan extends Plan

/** Plan for a single action. */
case class SingleActionPlan(action: Int) extends Plan {
  override def decide(state: Int) = Seq(action)
}

