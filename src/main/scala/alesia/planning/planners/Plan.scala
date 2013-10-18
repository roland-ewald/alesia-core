package alesia.planning.planners

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
  def decide(state: Int): Iterable[Int]
}

/** Trivial plan. */
trait EmptyPlan extends Plan {
  def decide(state: Int) = Iterable[Int]()
}

/** Plan for a single action. */
case class SingleActionPlan(action: Int) extends Plan {
  override def decide(state: Int) = Seq(action)
}

