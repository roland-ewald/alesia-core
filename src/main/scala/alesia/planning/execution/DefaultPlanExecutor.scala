package alesia.planning.execution

import scala.annotation.migration
import scala.collection.immutable.Stream.consWrapper
import scala.collection.mutable.ListBuffer
import alesia.planning.context.LocalJamesExecutionContext
import alesia.query.UserDomainEntity
import sessl.util.Logging
import alesia.query.WithStrictness
import alesia.query.TerminateWhen
import scala.annotation.tailrec
import alesia.planning.planners.PlanExecutionResult
import alesia.planning.planners.FullPlanExecutionResult
import alesia.planning.planners.FailurePlanExecutionResult

/**
 * Implements the [[PlanExecutor]] interface as a simple step-by-step execution of a plan.
 *
 * @author Roland Ewald
 */
object DefaultPlanExecutor extends PlanExecutor with Logging {

  /** Execute planning iteratively, by executing a stream of actions. */
  override def apply(s: ExecutionState): PlanExecutionResult = {
    val visitedStates = ListBuffer[ExecutionState]()
    try {
      executionStream(s, configureTermination(s)).foreach(visitedStates += _)
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

  /** Create termination condition from user preferences. */
  def configureTermination(state: ExecutionState): TerminationCondition =
    TerminationDisjunction(state.context.preferencesOf[TerminateWhen].map(_.condition): _*)

  /** Creates a stream of execution states. */
  def executionStream(state: ExecutionState, terminate: TerminationCondition): Stream[ExecutionState] =
    state #:: {
      val newState = DefaultPlanExecutor.iteratePlanExecution(state)
      if (newState.isFinished)
        Stream.empty
      else if (terminate(newState))
        throw new IllegalStateException("Plan execution was stopped prematurely.")
      else
        executionStream(newState, terminate)
    }

  /**
   * A full iteration of the plan execution.
   *  @param state current state
   *  @return new state
   */
  def iteratePlanExecution(state: ExecutionState): ExecutionState = {
    val currentState = state.problem.constructState(state.context.planState)
    // FIXME: this is only a temporary solution [Conjunction of not(goal) and currentState], 
    // correctly construct initial state instead
    val (actionIndex, newSelector) = DefaultPlanExecutor.selectAction(state,
      (!state.problem.goalState and currentState).id)
    val stateUpdate = DefaultPlanExecutor.executeAction(state, actionIndex)
    updateState(state, stateUpdate, newSelector)
  }

  /** Select action to be executed in current state. */
  def selectAction(state: ExecutionState, currentState: Int): (Int, ActionSelector) = {
    logger.info(s"Current state: ${state.problem.table.structureOf(currentState, state.problem.variableNames, "\t")}")
    val possibleActions = state.plan.decide(currentState)
    require(possibleActions.nonEmpty, "Plan has no actions for state.")
    val rv = state.context.actionSelector(possibleActions, state)
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
  def updateState(state: ExecutionState, update: StateUpdate, newSelector: ActionSelector): ExecutionState = {

    val strictPref = state.context.preferencesOf[WithStrictness].headOption
    if (!update.isConsistent)
      strictPref.map(_.strictness(s"Update is inconsists: ${update}"))

    logger.info(s"State update: ${update}\nCurrent state: ${state.problem.constructState(state.context.planState)}")

    val newPlanState = updatePlanState(state.context.planState, update.changes)
    val newEntities = updateEntities(state.context.entities, update.changes)
    val newLinks = updateLiteralLinks(state.context.entitiesForLiterals, update.addLinks, update.removeLinks, strictPref)

    logger.info(s"New state: ${state.problem.constructState(newPlanState)}")

    //TODO: generalize this away from Local + JAMES II
    ExecutionState(state.problem, state.plan,
      new LocalJamesExecutionContext(
        newEntities,
        state.context.preferences,
        newPlanState,
        newLinks,
        newSelector,
        state.context.statistics.actionExecuted))
  }

  def updatePlanState(previousState: PlanState, changes: Seq[Change]): PlanState = {
    val literalsToUpdate = changes.flatMap(c => c.literals.map((_, c.add)))
    val newPlanState = previousState.toMap ++ literalsToUpdate.toMap
    newPlanState.toSeq
  }

  def updateEntities(previousEntities: Seq[UserDomainEntity], changes: Seq[Change]): Seq[UserDomainEntity] = {
    val entitiesToChange = changes.groupBy(_.add).mapValues(_.flatMap(_.entities))
    val newEntities =
      previousEntities.toSet -- entitiesToChange.getOrElse(false, Set()) ++ entitiesToChange.getOrElse(true, Set())
    newEntities.toSeq
  }

  def updateLiteralLinks(previousLinks: LiteralLinks, additions: LinkChanges, removals: LinkChanges, strict: Option[WithStrictness]): LiteralLinks = {
    val newLinks = scala.collection.mutable.Map() ++ previousLinks
    for (r <- removals) {
      val links = newLinks.getOrElse(r._1, Seq())
      val oldSize = links.size
      newLinks(r._1) = links diff Seq(r._2)
      if (oldSize == newLinks(r._1).size)
        strict.map(_.strictness(
          s"Can't remove link from literal '${r._1}' to entity '${r._2}', as the entities are not linked."))
    }
    for (a <- additions)
      newLinks(a._1) = newLinks.getOrElse(a._1, Seq()) :+ a._2
    newLinks.mapValues(_.distinct).toMap
  }
}