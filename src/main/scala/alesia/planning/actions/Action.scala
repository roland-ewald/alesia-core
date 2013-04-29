package alesia.planning.actions

/**
 * General interface for actions. An action is completely pre-configured and ready to be executed.
 *
 *  @param [A] the context provider required to execute the action
 *  @author Roland Ewald
 */
trait Action[-A] {

  /** Execute the action. */
  def execute(implicit provider: A): Unit

  /** Get the results associated with all literals. */
  def resultFor(key: String): Option[AnyRef] = None

}

/** Super trait for all formulas to be specified by actions. */
sealed trait ActionFormula {

  /** The (user-friendly) name of the literal (or part of it). */
  def name: String

  def or(a: ActionFormula) = Disjunction(this, a)

  def and(a: ActionFormula) = Conjunction(this, a)
}

sealed trait Literal extends ActionFormula

/**
 * Represents a literal that is 'private' to this action, i.e. its actual name is unique (includes the action id).
 *  These literals allow actions to refer to their state variables.
 */
case class PrivateLiteral(val name: String) extends Literal

/**
 * Represents a literal that is public (shared between two or more actions).
 * It serves as a representation of the data that is exchanged between actions, i.e. their 'interface'.
 */
case class PublicLiteral(val name: String) extends Literal

/** Represents a conjunction. */
case class Conjunction(val left: ActionFormula, val right: ActionFormula) extends ActionFormula {
  def name = s"($left.name AND $right.name)"
}

/** Represents a conjunction. */
case class Disjunction(val left: ActionFormula, val right: ActionFormula) extends ActionFormula {
  def name = s"($left.name OR $right.name)"
}
