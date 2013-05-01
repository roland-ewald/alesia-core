package alesia.planning.preparation

import alesia.planning.PlanningProblem
import alesia.planning.context.ExecutionContext
import alesia.planning.context.SimpleExecutionContext
import alesia.query.UserSpecification
import alesia.query.Quantifier
import alesia.query.PredicateSubject
import alesia.query.PredicateRelation
import alesia.query.UserHypothesis
import scala.collection.mutable.ListBuffer
import alesia.query.Negation
import alesia.query.Conjunction
import alesia.query.Disjunction
import alesia.planning.actions.ActionRegistry

/**
 * Default plan preparation implementation.
 *
 * @see PlanPreparator
 *
 * @author Roland Ewald
 */
class DefaultPlanningPreparator extends PlanningPreparator {

  /** Refers to a single 'atomic' predicate relation (and to which subject and quantifier it relates). */
  type HypothesisElement = (Quantifier, PredicateSubject, PredicateRelation)

  override def preparePlanning(spec: UserSpecification): (PlanningProblem, ExecutionContext) = {

    // For each relevant variable, its representation is stored
    val elemToReprMap = ListBuffer[(Any, String)]()
    // For each representation, the actual variable is stored
    val repToElemMap = ListBuffer[(String, Any)]()

    //TODO: Define variables and actions
    val domainEntities = spec._1

    //TODO:
    //    ActionRegistry.actionSpecifications.filter(_.suitableFor(spec))

    //TODO: Define goal state 
    val hypothesis = spec._3

    val hypothesisElements = extractHypothesisElements(spec._3)

    val problem = new PlanningProblem() {
      val initialState = FalseVariable
      val goalState = FalseVariable
    }

    (problem, new SimpleExecutionContext(spec._2))
  }

  /** Extracts single hypothesis elements. */
  def extractHypothesisElements(h: UserHypothesis): Seq[HypothesisElement] =
    h.relation.atomicRelations.map((h.quantifier, h.subject, _))

}