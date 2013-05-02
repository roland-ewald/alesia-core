package alesia.planning.actions

/**
 * Super trait for all formulas to be specified by action specifications.
 *
 * @see ActionSpecification
 */
sealed trait ActionFormula {

  /** The (user-friendly) name of the literal (or part of it). */
  def name: String

  def or(a: ActionFormula) = Disjunction(this, a)

  def and(a: ActionFormula) = Conjunction(this, a)

  def unary_! = Negation(this)

  /** Declared public literals. */
  def publicLiterals: Seq[Literal] = ActionFormula.literalsOf(this).filter(_.isInstanceOf[PublicLiteral])

  /** Declared private literals. */
  def privateLiterals: Seq[Literal] = ActionFormula.literalsOf(this).filter(_.isInstanceOf[PrivateLiteral])
}

object ActionFormula {

  /** Get all literals used in an action formula. */
  def literalsOf(a: ActionFormula): Seq[Literal] = a match {
    case l: Literal => Seq(l)
    case u: UnaryOperator => literalsOf(u.expression)
    case b: BinaryOperator => literalsOf(b.left) ++ literalsOf(b.right)
    case _ => Seq()
  }
}

case object TrueFormula extends ActionFormula {
  def name = "TRUE"
}

case object FalseFormula extends ActionFormula {
  def name = "FALSE"
}

sealed trait Literal extends ActionFormula

sealed trait UnaryOperator extends ActionFormula {
  def expression: ActionFormula
}

sealed trait BinaryOperator extends ActionFormula {
  def left: ActionFormula
  def right: ActionFormula
}

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

case class Negation(val expression: ActionFormula) extends UnaryOperator {
  def name = s"!($expression.name)"
}

/** Represents a conjunction. */
case class Conjunction(val left: ActionFormula, val right: ActionFormula) extends BinaryOperator {
  def name = s"($left.name AND $right.name)"
}

/** Represents a conjunction. */
case class Disjunction(val left: ActionFormula, val right: ActionFormula) extends BinaryOperator {
  def name = s"($left.name OR $right.name)"
}