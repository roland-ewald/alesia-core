package alesia.planning.actions

import scala.reflect.ClassTag

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
  def publicLiterals = ActionFormula.literals[PublicLiteral](this)

  /** Declared private literals. */
  def privateLiterals = ActionFormula.literals[PrivateLiteral](this)
}

object ActionFormula {

  /** Get all literals used in an action formula. */
  def literals[X <: Literal: ClassTag](a: ActionFormula): Seq[X] = a match {
    case l: X => Seq(l)
    case u: UnaryOperator => literals(u.expression)
    case b: BinaryOperator => literals(b.left) ++ literals(b.right)
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