package alesia.planning.execution

import alesia.planning.context.ExecutionContext
import alesia.planning.plans.Plan
import alesia.planning.plans.PlanExecutionResult
import alesia.planning.PlanningProblem
import sessl.util.Logging
import alesia.planning.actions.Action

/**
 * Implements simple step-by-step execution of a plan.
 *
 * TODO: finish this
 *
 * @author Roland Ewald
 */
class DefaultPlanExecutor extends PlanExecutor with Logging {

  type TieBreaker = Iterable[Int] => Int

  val first: TieBreaker = _.head

  val random: TieBreaker = (x: Iterable[Int]) => {
    val randIndex = math.round(scala.math.random * x.size).toInt
    val elems = x.toArray
    elems(randIndex)
  }

  val tieBreaker = first

  def execute(d: ExecutionData): PlanExecutionResult = {

    var currentState = d.problem.initialState

    val actionIndex = selectAction(currentState.id, d.plan)
    val action = d.problem.declaredActions(actionIndex).toExecutableAction(d.context)

    // Execute action
    logger.info(s"""Executing action #${actionIndex}:
    				|	Declared action: ${d.problem.declaredActions(actionIndex)}
    				|	Planning action: ${d.problem.planningActions(actionIndex)}
    				|	Executable action: ${action}""".stripMargin)

    val stateUpdate = try {
      action.execute(d.context)
    } catch {
      case t: Throwable => {
        logger.error(s"Action ${action} could not be executed, ignoring it.", t)
        NoStateUpdate
      }
    }

    ???
  }

  def selectAction(currentState: Int, plan: Plan): Int = {
    val possibleActions = plan.decide(currentState)
    require(possibleActions.nonEmpty, "Plan has no actions for state.") //TODO: attempt repair & check its success?
    val action = tieBreaker(possibleActions)
    logger.info(s"Possible actions: ${possibleActions.mkString} --- choosing action ${action}")
    action
  }

}