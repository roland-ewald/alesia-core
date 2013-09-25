package alesia.planning.execution

import alesia.planning.actions.Literal
import alesia.query.UserDomainEntity

/**
 * Represents a state update, relates to both the [[PlanningProblem]] and the [[ExecutionContext]].
 *
 * @author Roland Ewald
 */
trait StateUpdate {
  def changes: Seq[Change]
}

case class Change(literals: Seq[Literal] = Seq(), entities: Seq[UserDomainEntity] = Seq(), add: Boolean = true)

case class SimpleStateUpdate(val changes: Change*) extends StateUpdate

object NoStateUpdate extends SimpleStateUpdate()