package alesia.planning.execution

import alesia.planning.context.ExecutionContext
import alesia.planning.plans.Plan
import alesia.planning.plans.PlanExecutionResult
import alesia.planning.PlanningProblem
import sessl.util.Logging

/**
 * Implements simple step-by-step execution of a plan execution.
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

    //start while (execControl.notConforms(data._1.goalState))

    val possibleActions: Iterable[Int] = d.plan.decide(currentState.id)

    // Select action
    logger.info(s"Potential actions: ${possibleActions.mkString}")
    require(possibleActions.nonEmpty, "Plan has no actions for state.") //TODO: attempt repair & check its success?

    val actionIndex = tieBreaker(possibleActions)
    val declaredAction = d.problem.declaredActions(actionIndex)
    val planningAction = d.problem.planningActions(actionIndex)
    val executableAction = declaredAction.toExecutableAction(d.context)

    // Execute action
    logger.info(s"""Executing action #${actionIndex}:
    				|	Declared action: ${declaredAction}
    				|	Planning action: ${planningAction}
    				|	Executable action: ${executableAction}""".stripMargin)
    val newContext = try {
      executableAction.execute(d.context)
    } catch {
      case t: Throwable => {
        logger.error(s"Action ${executableAction} could not be executed, ignoring it.", t)
        d.context
      }
    }

    ???
  }

}