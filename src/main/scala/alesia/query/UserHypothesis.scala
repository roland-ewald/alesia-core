package alesia.query

/**
 * Types for a small DSL to formulate hypotheses.
 */

/** Representations of quantities, e.g. '30%' or '5'. */
trait QuantitySpecification

/** the typical quantifier (as they are expected), and more general quantifier that allows a specific kind of quantification. */
case object forall extends Quantifier
case object exists extends Quantifier
case class some(val quantity: QuantitySpecification) extends Quantifier
case class probably(val what: Quantifier, val alpha: Double) extends Quantifier

/** All entities upon which the system can reason. */
sealed trait PredicateSubject
case class model(val pattern: String) extends PredicateSubject
case object model extends PredicateSubject

/** Different kinds of quantifiers. */
sealed trait Quantifier {
  def >>(p: PredicateSubject) = QuantifierAndSubject(this, p)
}

/** Implements second part of hypothesis syntax. */
case class QuantifierAndSubject(val q: Quantifier, val p: PredicateSubject) {
  def |(r: PredicateRelation) = SimpleHypothesis(q, p, r)
}

/** Define predicates. */
sealed trait PredicateRelation {

  def and(r: PredicateRelation) = Conjunction(this, r)

  def or(r: PredicateRelation) = Disjunction(this, r)

  def unary_! = Negation(this)

  def atomicRelations: Seq[PredicateRelation] = this match {
    case n: Negation => n.relation.atomicRelations
    case c: Conjunction => c.left.atomicRelations ++ c.right.atomicRelations
    case d: Disjunction => d.left.atomicRelations ++ d.right.atomicRelations
    case _ => Seq(this)
  }
}

case class Conjunction(val left: PredicateRelation, val right: PredicateRelation) extends PredicateRelation
case class Disjunction(val left: PredicateRelation, val right: PredicateRelation) extends PredicateRelation
case class Negation(val relation: PredicateRelation) extends PredicateRelation

case class hasProperty(val property: String) extends PredicateRelation
case class hasAttributeValue(val attribute: String, val value: Any) extends PredicateRelation
case class isFaster(val sim1Id:String, val sim2Id: String, val target: PredicateSubject) extends PredicateRelation

/** A complete (i.e. checkable) hypothesis as defined by the user. */
trait UserHypothesis {
  def quantifier: Quantifier
  def subject: PredicateSubject
  def relation: PredicateRelation
}

/** Default implementation of hypothesis. */
case class SimpleHypothesis(val quantifier: Quantifier, val subject: PredicateSubject, val relation: PredicateRelation) extends UserHypothesis

/** Dummy hypothesis, useful for testing. */
case object DummyHypothesis extends UserHypothesis {
  override val quantifier = exists
  override val subject = model
  override val relation = hasProperty("")
}
