package alesia.planning.preparation

import alesia.planning.actions.ActionFormula
import alesia.planning.PlanningProblem
import alesia.query.UserHypothesis

/**
 * Provides conversion methods for the constructs defined in [[alesia.query]].
 *
 * @author Roland Ewald
 *
 */
object FormulaConverter {

  /** Extracts single hypothesis elements. */
  def extractHypothesisElements(h: UserHypothesis): Seq[HypothesisElement] =
    h.relation.atomicRelations.map((h.quantifier, h.subject, _))

}