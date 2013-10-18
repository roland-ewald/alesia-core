package alesia.planning

import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.Literal
import alesia.planning.execution.PlanState
import scala.collection._
import alesia.planning.actions.ActionEffect
import alesia.planning.actions.ActionFormula

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
  lazy val functionByName = variablesByName.toMap

  /** For each variable/action, corresponding name in the planning domain. */
  private[this] val entityNames = mutable.Map[Any, String]()

  private[this] val variablesByName = mutable.Map[String, PlanningDomainFunction]()

  /** Associate a name in the planning domain with an entity (holding meta-data etc).*/
  private[this] def associateEntityWithName(a: Any, n: String): Unit = {
    require(!entityNames.isDefinedAt(a),
      s"Entity must be associated with a single name, but ${a} is associated with both ${n} and ${entityNames(a)}")
    entityNames(a) = n
  }

  /** Adds an action to the planning domain. */
  protected def addAction(a: ActionDeclaration): DomainAction = {

    def convertEffect(as: Seq[ActionEffect]): Seq[Effect] =
      as.map(a => Effect(convertFormula(a.condition), a.add.map(addVariable), a.del.map(addVariable), a.nondeterministic))

    val newAction = action(a.name, convertFormula(a.preCondition), convertEffect(a.effect): _*)
    associateEntityWithName(newAction, a.name)
    newAction
  }

  protected def convertFormula(a: ActionFormula): PlanningDomainFunction = {
    import alesia.planning.actions._
    a match {
      case Conjunction(l, r) => convertFormula(l) and convertFormula(r)
      case Disjunction(l, r) => convertFormula(l) or convertFormula(r)
      case Negation(r) => !convertFormula(r)
      case l: Literal => addVariable(l)
      case FalseFormula => FalseVariable
      case TrueFormula => TrueVariable
    }
  }

  /**
   * Adds a variable to the planning domain.
   *  @return true whether this is a new variable, otherwise false
   */
  protected def addVariable(l: Literal): PlanningDomainFunction = {
    variablesByName.getOrElseUpdate(l.name, {
      val newVariable = v(l.name)
      associateEntityWithName(newVariable, l.name)
      newVariable
    })
  }

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