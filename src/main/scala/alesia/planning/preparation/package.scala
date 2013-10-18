package alesia.planning

/**
 * This packages contains the code that interprets the user-specified problem (instances from [[alesia.query]]) and
 * converts it into a [[alesia.planning.DomainSpecificPlanningProblem]].
 *
 * @author Roland Ewald
 */
import alesia.query.Quantifier
import alesia.query.PredicateSubject
import alesia.query.PredicateRelation

package object preparation {

  /** Refers to a single 'atomic' predicate relation (and to which subject and quantifier it relates). */
  type HypothesisElement = (Quantifier, PredicateSubject, PredicateRelation)

}