package alesia.planning.plans

import alesia.planning.actions.Action
import alesia.planning.actions.ExperimentAction

/** Interface of a plan.
 *  @author Roland Ewald
 */
trait Plan {
  def children: Seq[Plan]
}

/** Plan for a single action. */
case class SingleActionPlan(action: ExperimentAction) extends Plan {
  def children = Nil
}

/** A plan containing a sequence of plan that depend on each other. These need to be executed in the correct (given) order. */
case class DependentActionPlan(children: Seq[Plan]) extends Plan

/** A plan containing independent action. The order in which the actions are executed does not matter. */
case class IndependentActionPlan(children: Seq[Plan]) extends Plan