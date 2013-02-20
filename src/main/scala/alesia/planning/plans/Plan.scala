package alesia.planning.plans

import alesia.planning.actions.Action
import alesia.planning.actions.ExperimentAction
import alesia.planning.context.Context

/**
 * Interface for a plan.
 *
 *  @author Roland Ewald
 */
trait Plan {

  /** Given the current context, decide for a sequence of actions, which might be carried out in parallel. */
  def decide(c: Context): Seq[ExperimentAction]

  /**
   * Decide upon action(s) based on the current state. TODO: merge with above method.
   * @return list of action indices of actions that should be tried
   */
  def decide(state: Int): Iterable[Int] = throw new UnsupportedOperationException 
}

/** Trivial plan. */
trait EmptyPlan extends Plan {
  override def decide(c: Context) = Seq()
}

/** Plan for a single action. */
case class SingleActionPlan(action: ExperimentAction) extends Plan {
  override def decide(c: Context) = Seq(action)
}

