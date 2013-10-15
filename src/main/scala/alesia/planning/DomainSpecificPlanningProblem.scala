package alesia.planning

import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.Literal
import alesia.planning.execution.PlanState

/**
 * Represents a [[alesia.planning.PlanningProblem]] that is tied to a specific application domain.
 *
 * Hence, it provides additional data structures that link the domain entities with the entities
 * in the planning domain.
 *
 * @author Roland Ewald
 */
abstract class DomainSpecificPlanningProblem extends PlanningProblem {

  /** Specifies which declared action corresponds to which action index. */
  val declaredActions: Map[Int, ActionDeclaration]

  /** Specifies which formal action (in the planning domain) corresponds to which action index. */
  val planningActions: Map[Int, DomainAction]

  /** Maps a variable name to its corresponding function. */
  val functionByName: Map[String, PlanningDomainFunction]

  /** Creates the representation of the [[alesia.planning.execution.PlanState]] in the planning domain. */
  def constructState(xs: PlanState): PlanningDomainFunction =
    conjunction {
      xs.map { x =>
        val elemFunction = functionByName(x._1)
        (if (x._2) elemFunction else !elemFunction)
      }
    }

  /** For debugging and logging. */
  lazy val detailedDescription: String = {

    def describe(x: Int) = table.structureOf(x, variableNames)

    def printEffectsDescription(es: Seq[Effect]): String = {
      for (e <- es)
        yield s"\tif(${describe(e.condition)}): add ${e.add.map(variableNames.getOrElse(_, "UNDEFINED")).mkString} || del ${e.del.map(variableNames.getOrElse(_, "UNDEFINED")).mkString} || ${if (e.nondeterministic) "?"}"
    }.mkString("\n")

    val rv = new StringBuilder
    rv.append("Variables:\n")
    for (varNum <- nextStateVarNums.keySet.toList.sorted)
      rv.append(s"#$varNum: ${variableNames.get(varNum).getOrElse("Error in variable numbering")}\n")

    rv.append("\nFunctions (by name):\n")
    functionByName.foreach(entry => rv.append(s"${entry._1}: ${entry._2}\n"))

    rv.append(s"\nInitial state: ${describe(initialState)}\n(raw: ${initialState})\n")

    rv.append("\nActions:\n")
    for (a <- actions)
      rv.append(s"""
        		|Action '${a.name}':
        		|- Precondition: ${table.structureOf(a.precondition, variableNames)}
          		|- Effects: \n${printEffectsDescription(a.effects)}""".stripMargin)

    rv.append(s"\n\nGoal: ${table.structureOf(goalState, variableNames)}\n(raw: ${goalState})\n\n")
    rv.toString
  }
}