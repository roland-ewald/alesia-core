package alesia.planning.execution

import alesia.planning.actions.Literal
import alesia.query.UserDomainEntity

/**
 * Represents a state update, relates to both the [[PlanningProblem]] and the [[ExecutionContext]].
 *
 * @author Roland Ewald
 */
sealed trait StateUpdate {

  /** List of changes regarding the planner's state and the available user domain entities. */
  def changes: Seq[Change]

  /** Links to add between literals in the planning domain and user domain entities. */
  def addLinks: LinkChanges

  /** Links to remove between literals in the planning domain and user domain entities. */
  def removeLinks: LinkChanges

  /**
   * Consistency checks: is a literal changed both to true and to false? Holds the same for entities and entity links?
   */
  lazy val isConsistent: Boolean =
    inconsistentChanges(_.literals).isEmpty && inconsistentChanges(_.entities).isEmpty &&
      (addLinks intersect removeLinks).isEmpty

  /** Return all entities of type X that are included in add-changes and remove-changes. */
  def inconsistentChanges[X](f: Change => Seq[X]): Set[X] = {
    val changed = changes.groupBy(_.add).mapValues(_.flatMap(f).toSet)
    changed.getOrElse(true, Set()) intersect changed.getOrElse(false, Set())
  }
}

object StateUpdate {

  def apply(changes: Change*): SimpleStateUpdate = SimpleStateUpdate(Seq(), Seq(), changes)

  def specify(changes: Seq[Change] = Seq(), add: Map[String, UserDomainEntity] = Map(), del: Map[String, UserDomainEntity] = Map()): SimpleStateUpdate =
    SimpleStateUpdate(add.toSeq, del.toSeq, changes)
}

abstract class Change(val literals: Seq[String] = Seq(), val entities: Seq[UserDomainEntity] = Seq(), val add: Boolean = true)

case class AddLiterals(addLiterals: String*) extends Change(addLiterals)
case class RemoveLiterals(removeLiterals: String*) extends Change(removeLiterals, add = false)

case class AddEntities(addEntities: UserDomainEntity*) extends Change(entities = addEntities)
case class RemoveEntities(removeEntities: UserDomainEntity*) extends Change(entities = removeEntities, add = false)

case class SimpleStateUpdate(val addLinks: LinkChanges, val removeLinks: LinkChanges, val changes: Seq[Change]) extends StateUpdate

object NoStateUpdate extends SimpleStateUpdate(Seq(), Seq(), Seq())