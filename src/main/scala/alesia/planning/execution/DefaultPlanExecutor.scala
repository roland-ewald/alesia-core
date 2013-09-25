package alesia.planning.execution

import alesia.planning.context.ExecutionContext
import alesia.planning.plans.Plan
import alesia.planning.plans.PlanExecutionResult
import alesia.planning.PlanningProblem
import sessl.util.Logging
import alesia.planning.actions.Action
import alesia.planning.context.LocalJamesExecutionContext
import java.util.Collections.EmptySet

/**
 * Implements simple step-by-step execution of a plan.
 *
 * TODO: finish this
 *
 * @author Roland Ewald
 */
class DefaultPlanExecutor extends PlanExecutor with Logging {

  /** How to break ties in case multiple actions, represented by their indices, can be chosen. */
  type TieBreaker = Iterable[Int] => Int

  val first: TieBreaker = _.head

  val random: TieBreaker = (x: Iterable[Int]) => {
    val randIndex = math.round(scala.math.random * x.size).toInt
    val elems = x.toArray
    elems(randIndex)
  }

  val tieBreaker = first

  def execute(d: ExecutionData): PlanExecutionResult = {

    val currentState = d.problem.constructState(d.context.planState)
    val actionIndex = selectAction(currentState.id, d.plan)
    val stateUpdate = executeAction(actionIndex, d)
    val newExecutionData = updateState(d, stateUpdate, d.context)

    println(newExecutionData)

    ???
  }

  /** Select action to be executed in current state. */
  def selectAction(currentState: Int, plan: Plan): Int = {
    val possibleActions = plan.decide(currentState)
    require(possibleActions.nonEmpty, "Plan has no actions for state.") //TODO: attempt repair & check its success?
    val action = tieBreaker(possibleActions)
    logger.info(s"Possible actions: ${possibleActions.mkString} --- choosing action ${action}")
    action
  }

  /** Execute selected action. */
  def executeAction(actionIndex: Int, d: ExecutionData): StateUpdate = {
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

  def updateState(d: ExecutionData, update: StateUpdate, context: ExecutionContext): ExecutionData = {
    logger.info(s"State update: ${update}")

    // Update planning state
    val literalsToUpdate = update.changes.flatMap(c => c.literals.map((_, c.add)))
    val newPlanState = (d.context.planState.toMap ++ literalsToUpdate.toMap).toSeq

    //Update execution context
    val entitiesToChange = update.changes.groupBy(_.add).mapValues(_.flatMap(_.entities))
    val newEntities = d.context.entities.toSet -- entitiesToChange.getOrElse(false, Set()) ++ entitiesToChange.getOrElse(true, Set())

    //TODO: generalize this
    ExecutionData(d.problem, d.plan, new LocalJamesExecutionContext(newEntities.toSeq, d.context.preferences, newPlanState))
  }

}