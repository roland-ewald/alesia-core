package alesia.planning.execution

import alesia.planning.actions.Literal
import alesia.query.UserDomainEntity

/**
 * Represents a state update, relates to both the [[alesia.planning.PlanningProblem]] and the
 * [[alesia.planning.context.ExecutionContext]].
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
   * Checks whether a literal is changed to both true and false, whether an entity is added and removed, and
   * whether the same link is added or removed.
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

/**
 * Simple implementation of [[StateUpdate]].
 * @param changes the changes regarding literals and entities
 * @param addLinks the links between literals and domain entities to be added
 * @param removeLinks the links between literals and domain entities to be removed
 */
case class SimpleStateUpdate(
  val changes: Seq[Change],
  val addLinks: LinkChanges,
  val removeLinks: LinkChanges) extends StateUpdate {

  lazy val addedEntities = addLinks.map(_._2)

  lazy val removedEntities = removeLinks.map(_._2)

}

/** Factory methods for [[StateUpdate]].*/
object StateUpdate {

  /** Create update only containing changes. */
  def apply(changes: Change*): SimpleStateUpdate = SimpleStateUpdate(changes, Seq(), Seq())

  /** Full specification of update. */
  def specify(changes: Seq[Change] = Seq(),
    add: Map[String, UserDomainEntity] = Map(), remove: Map[String, UserDomainEntity] = Map()): SimpleStateUpdate =
    SimpleStateUpdate(changes, add.toSeq, remove.toSeq)
}

/**
 * Represents a change in the [[alesia.planning.context.ExecutionContext]] of the
 *  [[alesia.planning.execution.ExecutionState]], therefore part of a [[StateUpdate]].
 */
sealed abstract class Change(val literals: Seq[String], val entities: Seq[UserDomainEntity], val add: Boolean)

case class AddLiterals(addLiterals: String*) extends Change(addLiterals, Seq(), true)
case class RemoveLiterals(removeLiterals: String*) extends Change(removeLiterals, Seq(), add = false)

case class AddEntities(addEntities: UserDomainEntity*) extends Change(Seq(), addEntities, true)
case class RemoveEntities(removeEntities: UserDomainEntity*) extends Change(Seq(), removeEntities, false)

object NoStateUpdate extends SimpleStateUpdate(Seq(), Seq(), Seq())