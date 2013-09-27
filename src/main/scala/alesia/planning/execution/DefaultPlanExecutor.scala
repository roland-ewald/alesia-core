package alesia.planning.execution

import alesia.planning.PlanningProblem
import alesia.planning.actions.Action
import alesia.planning.context.ExecutionContext
import alesia.planning.context.LocalJamesExecutionContext
import alesia.planning.plans.FullPlanExecutionResult
import alesia.planning.plans.Plan
import alesia.planning.plans.FailurePlanExecutionResult
import alesia.planning.plans.PlanExecutionResult
import sessl.util.Logging
import scala.collection.mutable.ListBuffer

/**
 * Implements simple step-by-step execution of a plan.
 *
 * TODO: finish this
 *
 * @author Roland Ewald
 */
class DefaultPlanExecutor extends PlanExecutor with Logging {

  //TODO: This is to prevent infinite loops; generalize via UserPreferences
  val maxTries = 100

  /** How to break ties in case multiple actions, represented by their indices, can be chosen. */
  type TieBreaker = Iterable[Int] => Int

  val first: TieBreaker = _.head

  val random: TieBreaker = (x: Iterable[Int]) => {
    val randIndex = math.round(scala.math.random * x.size).toInt
    val elems = x.toArray
    elems(randIndex)
  }

  val tieBreaker = first

  /** Execute planning. */
  def execute(s: ExecutionState): PlanExecutionResult = {
    val visitedStates = ListBuffer[ExecutionState]()
    try {
      executionStream(s).foreach(visitedStates += _._1)
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
  def executionStream(state: ExecutionState, counter: Int = 0): Stream[(ExecutionState, Int)] =
    (state, counter) #:: {
      val currentState = state.problem.constructState(state.context.planState)
      // FIXME: this is only a temporary solution [Conjunction of not(goal) and currentState], correctly construct initial state instead
      val actionIndex = selectAction(state, (!state.problem.goalState and currentState).id)
      val stateUpdate = executeAction(state, actionIndex)
      val newState = updateState(state, stateUpdate)
      if (newState.isFinished)
        Stream.empty
      else if (stopDueToPreferences(newState) || counter >= maxTries)
        throw new IllegalStateException("Plan execution was stopped prematurely.")
      else
        executionStream(newState, counter + 1)
    }

  /** Select action to be executed in current state. */
  def selectAction(state: ExecutionState, currentState: Int): Int = {
    logger.info(s"Current state: ${state.problem.table.structureOf(currentState, state.problem.variableNames, "\t")}")
    val possibleActions = state.plan.decide(currentState)
    require(possibleActions.nonEmpty, "Plan has no actions for state.") //TODO: attempt repair & check its success?
    val action = tieBreaker(possibleActions)
    logger.info(s"Possible actions: ${possibleActions.mkString} --- choosing action ${action}")
    action
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

    // Update planning state
    val literalsToUpdate = update.changes.flatMap(c => c.literals.map((_, c.add)))
    val newPlanState = (state.context.planState.toMap ++ literalsToUpdate.toMap).toSeq

    //Update execution context
    val entitiesToChange = update.changes.groupBy(_.add).mapValues(_.flatMap(_.entities))
    val newEntities = state.context.entities.toSet -- entitiesToChange.getOrElse(false, Set()) ++ entitiesToChange.getOrElse(true, Set())

    //Update linked entities -- TODO: Move to extra method?
    var literalLinks = scala.collection.mutable.Map() ++ state.context.entitiesForLiterals
    for (link <- update.removeLinks)
      literalLinks(link._1) = literalLinks.getOrElse(link._1, Seq()) diff Seq(link._2)
    for (link <- update.addLinks)
      literalLinks(link._1) = literalLinks.getOrElse(link._1, Seq()) :+ link._2

    logger.info(s"New state: ${state.problem.constructState(newPlanState)}")

    //TODO: generalize this away from Local + JAMES II
    ExecutionState(state.problem, state.plan, new LocalJamesExecutionContext(newEntities.toSeq, state.context.preferences, newPlanState, literalLinks.mapValues(_.distinct).toMap))
  }

}