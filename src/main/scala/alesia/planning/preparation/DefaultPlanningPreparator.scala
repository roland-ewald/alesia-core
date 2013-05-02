package alesia.planning.preparation

import scala.collection.mutable.ListBuffer

import alesia.planning.PlanningProblem
import alesia.planning.actions.ActionRegistry
import alesia.planning.context.ExecutionContext
import alesia.planning.context.SimpleExecutionContext
import alesia.query.PredicateRelation
import alesia.query.PredicateSubject
import alesia.query.Quantifier
import alesia.query.UserHypothesis
import alesia.query.UserSpecification

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

    //TODO: Variable names should be mapped to elements of the context, actions should be mapped to their specifications (so that executable actions can be created easily)

    val suitableActionSpecs = ActionRegistry.actionSpecifications.filter(_.suitableFor(spec))

    val publicLiterals = suitableActionSpecs.flatMap(s => s.effect.publicLiterals ++ s.preCondition.publicLiterals)

    //TODO: Define variables
    val domainEntities = spec._1

    println(domainEntities)

    //TODO: Define actions on variables, and new instances of action-specific variables

    println(suitableActionSpecs)

    //TODO: Define goal state 
    val hypothesis = spec._3

    val hypothesisElements = extractHypothesisElements(spec._3)

    val problem = new PlanningProblem() {
      val initialState = FalseVariable
      val goalState = FalseVariable
    }

    println(problem)

    (problem, new SimpleExecutionContext(spec._2))
  }

  /** Extracts single hypothesis elements. */
  def extractHypothesisElements(h: UserHypothesis): Seq[HypothesisElement] =
    h.relation.atomicRelations.map((h.quantifier, h.subject, _))

}