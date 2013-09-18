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

    if (possibleActions.isEmpty) {
      logger.warn(s"Plan has no actions for state")
      throw new IllegalStateException
      //TODO: attempt repair & check its success
    }

    val pick = tieBreaker(possibleActions)

    val planActions = data._1.actions(pick)

    println("Planning on executing:" + planActions)

    println(possibleActions.mkString)

    ???
  }

}