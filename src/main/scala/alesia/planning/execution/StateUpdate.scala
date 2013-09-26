package alesia.planning.execution

import alesia.planning.actions.Literal
import alesia.query.UserDomainEntity

/**
 * Represents a state update, relates to both the [[PlanningProblem]] and the [[ExecutionContext]].
 *
 * @author Roland Ewald
 */
trait StateUpdate {

  //TODO: Add consistency checks: is a literal changed both to true and to false?

  def changes: Seq[Change]

  def addLinks: LinkChanges

  def removeLinks: LinkChanges //TODO: finish this
}

object StateUpdate {

  def simple(changes: Change*): SimpleStateUpdate = SimpleStateUpdate(Seq(), Seq(), changes)
}

abstract class Change(val literals: Seq[String] = Seq(), val entities: Seq[UserDomainEntity] = Seq(), val add: Boolean = true)

case class AddLiterals(addLiterals: String*) extends Change(addLiterals)
case class RemoveLiterals(removeLiterals: String*) extends Change(removeLiterals, add = false)

case class AddEntities(addEntities: UserDomainEntity*) extends Change(entities = addEntities)
case class RemoveEntities(removeEntities: UserDomainEntity*) extends Change(entities = removeEntities, add = false)

case class SimpleStateUpdate(val addLinks: LinkChanges, val removeLinks: LinkChanges, val changes: Seq[Change]) extends StateUpdate

object NoStateUpdate extends SimpleStateUpdate(Seq(), Seq(), Seq())