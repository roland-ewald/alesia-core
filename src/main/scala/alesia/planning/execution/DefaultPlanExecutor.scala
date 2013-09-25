package alesia.planning.execution

import alesia.planning.PlanningProblem
import alesia.planning.actions.Action
import alesia.planning.context.ExecutionContext
import alesia.planning.context.LocalJamesExecutionContext
import alesia.planning.plans.FullPlanResults
import alesia.planning.plans.Plan
import alesia.planning.plans.PlanExecutionFailureResult
import alesia.planning.plans.PlanExecutionResult
import sessl.util.Logging

/**
 * Implements simple step-by-step execution of a plan.
 *
 * TODO: finish this
 *
 * @author Roland Ewald
 */
class DefaultPlanExecutor extends PlanExecutor with Logging {

  //TODO: This is to prevent invinite loops; generalize via UserPreferences
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
    val stateStream = executionStream(s)
    val states = stateStream.iterator
    try {
      states.zipWithIndex.takeWhile { state =>
        !stopDueToPreferences(state._1) && state._2 < maxTries
      }
    } catch {
      case t: Throwable => {
        logger.error("Plan execution failed", t)
        PlanExecutionFailureResult(stateStream, t)
      }
    }
    logger.info(s"Plan execution finished  --- ${stateStream.size} actions executed.")
    FullPlanResults(stateStream)
  }

  def stopDueToPreferences(s: ExecutionState): Boolean = false //TODO: finish this

  /** Creates a stream of execution states. */
  def executionStream(state: ExecutionState): Stream[ExecutionState] =
    state #:: {
      val currentState = state.problem.constructState(state.context.planState)
      val actionIndex = selectAction(currentState.id, state)
      val stateUpdate = executeAction(actionIndex, state)
      val newState = updateState(state, stateUpdate, state.context)
      if (newState.isFinished)
        Stream.empty
      else
        executionStream(newState)
    }

  /** Select action to be executed in current state. */
  def selectAction(currentState: Int, state: ExecutionState): Int = {
    logger.info(s"Current state: ${state.problem.table.structureOf(currentState, state.problem.variableNames, "\t")}")
    val possibleActions = state.plan.decide(currentState)
    require(possibleActions.nonEmpty, "Plan has no actions for state.") //TODO: attempt repair & check its success?
    val action = tieBreaker(possibleActions)
    logger.info(s"Possible actions: ${possibleActions.mkString} --- choosing action ${action}")
    action
  }

  /** Execute selected action. */
  def executeAction(actionIndex: Int, d: ExecutionState): StateUpdate = {
    val action = d.problem.declaredActions(actionIndex).toExecutableAction(d.context)
    logger.info(s"""Executing action #${actionIndex}:
    				|	Declared action: ${d.problem.declaredActions(actionIndex)}
    				|	Planning action: ${d.problem.planningActions(actionIndex)}
    				|	Executable action: ${action}""".stripMargin)
    try {
      action.execute(d.context)
    } catch {
      case t: Throwable => {
        logger.error(s"Action ${action} could not be executed, ignoring it.", t)
        NoStateUpdate
      }
    }
  }

  /** Update execution context and plan state after an action has been executed. */
  def updateState(d: ExecutionState, update: StateUpdate, context: ExecutionContext): ExecutionState = {
    logger.info(s"State update: ${update}")

    // Update planning state
    val literalsToUpdate = update.changes.flatMap(c => c.literals.map((_, c.add)))
    val newPlanState = (d.context.planState.toMap ++ literalsToUpdate.toMap).toSeq

    //Update execution context
    val entitiesToChange = update.changes.groupBy(_.add).mapValues(_.flatMap(_.entities))
    val newEntities = d.context.entities.toSet -- entitiesToChange.getOrElse(false, Set()) ++ entitiesToChange.getOrElse(true, Set())

    //TODO: generalize this
    ExecutionState(d.problem, d.plan, new LocalJamesExecutionContext(newEntities.toSeq, d.context.preferences, newPlanState))
  }

}