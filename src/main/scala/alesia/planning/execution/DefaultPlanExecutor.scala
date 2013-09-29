package alesia.planning.execution

import scala.annotation.migration
import scala.collection.immutable.Stream.consWrapper
import scala.collection.mutable.ListBuffer

import alesia.planning.context.LocalJamesExecutionContext
import alesia.planning.plans.FailurePlanExecutionResult
import alesia.planning.plans.FullPlanExecutionResult
import alesia.planning.plans.PlanExecutionResult
import alesia.query.UserDomainEntity
import sessl.util.Logging

/**
 * Implements the [[PlanExecutor]] interface as a simple step-by-step execution of a plan.
 *
 * @author Roland Ewald
 */
class DefaultPlanExecutor extends PlanExecutor with Logging {

  //TODO: This is to prevent infinite loops; generalize via UserPreferences
  val maxTries = 4
  val initialActionSelector: ActionSelector = FirstActionSelector //TODO: generalize via UserPreferences as well

  /** Execute planning. */
  override def apply(s: ExecutionState): PlanExecutionResult = {
    val visitedStates = ListBuffer[ExecutionState]()
    try {
      executionStream(s, 0, initialActionSelector).foreach(visitedStates += _._1)
    } catch {
      case t: Throwable => {
        logger.error("Plan execution failed", t)
        return FailurePlanExecutionResult(visitedStates.toVector, t)
      }
    }
    val trace = visitedStates.toVector
    logger.info(s"Plan execution finished  --- ${visitedStates.size} actions executed.")
    FullPlanExecutionResult(trace)
  }

  def stopDueToPreferences(s: ExecutionState): Boolean = false //TODO: finish this

  /** Creates a stream of execution states. */
  def executionStream(state: ExecutionState, counter: Int, selector: ActionSelector): Stream[(ExecutionState, Int, ActionSelector)] =
    (state, counter, selector) #:: {
      val (newState, newSelector) = DefaultPlanExecutor.iteratePlanExecution(state, selector)
      if (newState.isFinished)
        Stream.empty
      else if (stopDueToPreferences(newState) || counter >= maxTries)
        throw new IllegalStateException("Plan execution was stopped prematurely.")
      else
        executionStream(newState, counter + 1, newSelector)
    }

}

/**
 * General methods to update the execution state.
 */
object DefaultPlanExecutor extends Logging {

  def iteratePlanExecution(state: ExecutionState, selector: ActionSelector): (ExecutionState, ActionSelector) = {
    val currentState = state.problem.constructState(state.context.planState)
    // FIXME: this is only a temporary solution [Conjunction of not(goal) and currentState], correctly construct initial state instead
    val (actionIndex, newSelector) = DefaultPlanExecutor.selectAction(state, (!state.problem.goalState and currentState).id, selector)
    val stateUpdate = DefaultPlanExecutor.executeAction(state, actionIndex)
    (updateState(state, stateUpdate), newSelector)
  }

  /** Select action to be executed in current state. */
  def selectAction(state: ExecutionState, currentState: Int, selector: ActionSelector): (Int, ActionSelector) = {
    logger.info(s"Current state: ${state.problem.table.structureOf(currentState, state.problem.variableNames, "\t")}")
    val possibleActions = state.plan.decide(currentState)
    require(possibleActions.nonEmpty, "Plan has no actions for state.") //TODO: attempt repair & check its success?
    val rv = selector(possibleActions, state)
    logger.info(s"Possible actions: ${possibleActions.mkString} --- choosing action ${rv._1}")
    rv
  }

  /** Execute selected action. */
  def executeAction(state: ExecutionState, actionIndex: Int): StateUpdate = {
    try {
      val action = state.problem.declaredActions(actionIndex).toExecutableAction(state.context)
      logger.info(s"""Executing action #${actionIndex}:
    				|	Declared action: ${state.problem.declaredActions(actionIndex)}
    				|	Planning action: ${state.problem.planningActions(actionIndex)}
    				|	Executable action: ${action}""".stripMargin)
      action.execute(state.context)
    } catch {
      case t: Throwable => {
        logger.error(s"Action ${actionIndex} could not be executed, ignoring it.", t)
        NoStateUpdate
      }
    }
  }

  /** Update execution context and plan state after an action has been executed. */
  def updateState(state: ExecutionState, update: StateUpdate): ExecutionState = {

    logger.info(s"State update: ${update}\nCurrent state: ${state.problem.constructState(state.context.planState)}")

    val newPlanState = updatePlanState(state.context.planState, update.changes)
    val newEntities = updateEntities(state.context.entities, update.changes)
    val newLinks = updateLiteralLinks(state.context.entitiesForLiterals, update.addLinks, update.removeLinks)

    logger.info(s"New state: ${state.problem.constructState(newPlanState)}")

    //TODO: generalize this away from Local + JAMES II
    ExecutionState(state.problem, state.plan,
      new LocalJamesExecutionContext(newEntities, state.context.preferences, newPlanState, newLinks))
  }

  def updatePlanState(previousState: PlanState, changes: Seq[Change]): PlanState = {
    val literalsToUpdate = changes.flatMap(c => c.literals.map((_, c.add)))
    val newPlanState = previousState.toMap ++ literalsToUpdate.toMap
    newPlanState.toSeq
  }

  def updateEntities(previousEntities: Seq[UserDomainEntity], changes: Seq[Change]): Seq[UserDomainEntity] = {
    val entitiesToChange = changes.groupBy(_.add).mapValues(_.flatMap(_.entities))
    val newEntities = previousEntities.toSet -- entitiesToChange.getOrElse(false, Set()) ++ entitiesToChange.getOrElse(true, Set())
    newEntities.toSeq
  }

  def updateLiteralLinks(previousLinks: LiteralLinks, additions: LinkChanges, removals: LinkChanges): LiteralLinks = {
    var newLinks = scala.collection.mutable.Map() ++ previousLinks
    for (r <- removals) //TODO: warn in case there was nothing to be removed?
      newLinks(r._1) = newLinks.getOrElse(r._1, Seq()) diff Seq(r._2)
    for (a <- additions)
      newLinks(a._1) = newLinks.getOrElse(a._1, Seq()) :+ a._2
    newLinks.mapValues(_.distinct).toMap
  }
}