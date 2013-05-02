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
import alesia.planning.actions.Literal

/**
 * Default plan preparation implementation.
 *
 * TODO: Variable names should be mapped to elements of the context, actions should be mapped to their specifications (so that executable actions can be created easily)
 *
 * @see PlanPreparator
 *
 * @author Roland Ewald
 */
class DefaultPlanningPreparator extends PlanningPreparator {

  /** Refers to a single 'atomic' predicate relation (and to which subject and quantifier it relates). */
  type HypothesisElement = (Quantifier, PredicateSubject, PredicateRelation)

  /** For each variable/action, corresponding name in the planning domain. */
  private[this] var nameForEntity = scala.collection.mutable.Map[Any, String]()

  /** For each name in the planning domain, corresponding variable/action.*/
  private[this] var entityForName = scala.collection.mutable.Map[String, Any]()

  private[this] var variableNames = ListBuffer[String]()

  private[this] var actionNames = ListBuffer[String]()

  lazy val varNames = variableNames.toList

  lazy val actNames = actionNames.toList

  /** Associate a name in the planning domain with an entity (holding meta-data etc).*/
  private[this] def associateEntityWithName(a: Any, n: String) = {
    nameForEntity(a) = n
    entityForName(n) = a
  }

  /** Adds a variable to the planning domain. */
  private[this] def addVariable(l: Literal) = {
    associateEntityWithName(l, l.name)
    variableNames += l.name
  }

  override def preparePlanning(spec: UserSpecification): (PlanningProblem, ExecutionContext) = {

    val suitableActionSpecs = ActionRegistry.actionSpecifications.filter(_.suitableFor(spec))

    val publicLiterals = suitableActionSpecs.flatMap(s => s.effect.publicLiterals ++ s.preCondition.publicLiterals)
    publicLiterals.foreach(addVariable)

    //TODO: Define variables
    val domainEntities = spec._1

    println(domainEntities)

    //TODO: Define actions on variables, and new instances of action-specific variables

    println(suitableActionSpecs)

    //TODO: Define goal state 
    val hypothesis = spec._3

    val hypothesisElements = extractHypothesisElements(spec._3)

    // TODO: Make custom class out of this
    val problem = new PlanningProblem() {

      val functionByName = varNames.map { varName =>
        (varName, v(varName))
      }.toMap

      val initialState = FalseVariable
      val goalState = FalseVariable
    }

    //TODO: full description of planning problem should be logged 
    println(problem)
    println(problem.functionByName)

    (problem, new SimpleExecutionContext(spec._2))
  }

  /** Extracts single hypothesis elements. */
  def extractHypothesisElements(h: UserHypothesis): Seq[HypothesisElement] =
    h.relation.atomicRelations.map((h.quantifier, h.subject, _))

}