package alesia.planning.preparation

import scala.collection.mutable
import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.PlanningProblem
import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.ActionEffect
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.ActionRegistry
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.Literal
import alesia.planning.actions.PublicLiteral
import alesia.planning.context.ExecutionContext
import alesia.planning.context.LocalJamesExecutionContext
import alesia.planning.execution.ActionSelector
import alesia.planning.execution.FirstActionSelector
import alesia.query.PredicateRelation
import alesia.query.PredicateSubject
import alesia.query.Quantifier
import alesia.query.StartWithActionSelector
import alesia.query.UserHypothesis
import alesia.query.exists
import alesia.utils.misc.CollectionHelpers
import sessl.util.Logging
import alesia.planning.actions.AllDeclaredActions
import alesia.query.ProblemSpecification
import alesia.planning.context.ExecutionStatistics

/**
 * Default [[alesia.planning.preparation.PlanningPreparator]] implementation.
 *
 * @author Roland Ewald
 */
class DefaultPlanningPreparator extends PlanningPreparator with Logging {

  override def preparePlanning(spec: ProblemSpecification): (DomainSpecificPlanningProblem, ExecutionContext) = {

    val allDeclaredActions = DefaultPlanningPreparator.retrieveDeclaredActions(spec)

    val declaredActionsList = allDeclaredActions.flatMap(_._2)
    logger.info(s"\n\nDeclared actions:\n=======================\n\n${declaredActionsList.mkString("\n")}, for ${allDeclaredActions.size} specifications.")

    val problem = new DefaultPlanningProblem(spec, declaredActionsList)

    logger.info(s"\n\nGenerated planning problem:\n===========================\n\n${problem.detailedDescription}")
    (problem, //TODO: Generalize this:
      new LocalJamesExecutionContext(spec._1, spec._2, problem.inititalPlanState.toList,
        actionSelector = DefaultPlanningPreparator.initializeActionSelector(spec),
        statistics = ExecutionStatistics()))
  }

}

object DefaultPlanningPreparator extends Logging {

  /** The maximal number of rounds before action creation is aborted. */
  val maxRounds = 100

  /**
   * Creates a set of actions by repeatedly querying all action specification with the current number of
   * available actions and asking them to create additional ones. If no new actions are created (a fixed point),
   * the algorithm stops.
   *
   * @param spec the problem specification
   */
  def retrieveDeclaredActions(spec: ProblemSpecification): AllDeclaredActions = {

    val actionSpecs = ActionRegistry.actionSpecifications
    val declaredActions = mutable.Map[ActionSpecification, Seq[ActionDeclaration]]()

    var newActionsDeclared = true
    var counter = 0
    actionSpecs.foreach(declaredActions(_) = Seq())

    while (newActionsDeclared && counter < maxRounds) {
      logger.debug(s"Round $counter of declared action retrieval")
      val currentActions = declaredActions.toMap
      val newActions = actionSpecs.map(x => (x, x.declareConcreteActions(spec, currentActions))).toMap
      for (a <- newActions; newDeclarations <- a._2) {
        declaredActions(a._1) = declaredActions(a._1) ++ newDeclarations
        logger.debug(s"Adding action declaration $a._2 for specification $a._1")
      }
      newActionsDeclared = newActions.values.exists(_.nonEmpty)
      counter += 1
    }

    if (counter == maxRounds)
      logger.warn(s"Action declaration during plan preparation took the maximum number of rounds! ($maxRounds)")

    declaredActions.toMap
  }

  def initializeActionSelector(spec: ProblemSpecification): ActionSelector = {
    val actionSelectors = CollectionHelpers.filterType[StartWithActionSelector](spec._2)
    require(actionSelectors.size < 2,
      s"Aborting; multiple action selectors have been defined for initialization:${actionSelectors.mkString}")
    actionSelectors.headOption.map(_.selector).getOrElse(FirstActionSelector)
  }
}