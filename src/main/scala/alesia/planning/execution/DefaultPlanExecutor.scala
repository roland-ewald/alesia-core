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

  def execute(data: ExecutionData): PlanExecutionResult = {

    var currentState = data._1.initialState

    //start while (execControl.notConforms(data._1.goalState))

    val possibleActions: Iterable[Int] = data._2.decide(currentState.id)

    // Select action
    logger.info(s"Potential actions: ${possibleActions.mkString}")
    require(possibleActions.nonEmpty, "Plan has no actions for state.") //TODO: attempt repair & check its success?

    val actionIndex = tieBreaker(possibleActions)
    val declaredAction = data._1.declaredActions(actionIndex)
    val planningAction = data._1.planningActions(actionIndex)
    val executableAction = declaredAction.toExecutableAction(data._3)

    // Execute action
    logger.info(s"""Executing action #${actionIndex}:
    				|	Declared action: ${declaredAction}
    				|	Planning action: ${planningAction}
    				|	Executable action: ${executableAction}""".stripMargin)

    ???
  }

}