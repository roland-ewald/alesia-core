package alesia.planning.preparation

import alesia.planning.actions.ActionEffect
import alesia.planning.actions.ActionDeclaration
import alesia.query.ProblemSpecification
import alesia.query.UserHypothesis
import alesia.planning.actions.PublicLiteral
import alesia.query.PredicateRelation
import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.execution.PlanState
import alesia.planning.actions.Literal
import alesia.planning.actions.ActionFormula

/**
 * Default implementation of how to construct a [[DomainSpecificPlanningProblem]] from a [[ProblemSpecification]].
 *
 * @author Roland Ewald
 */
class DefaultPlanningProblem(val spec: ProblemSpecification, val declaredActionsList: Iterable[ActionDeclaration])
  extends DomainSpecificPlanningProblem {

  override val declaredActions: Map[Int, ActionDeclaration] = declaredActionsList.zipWithIndex.map(x => (x._2, x._1)).toMap

  override val planningActions = {
    declaredActionsList.zipWithIndex map { (a: (ActionDeclaration, Int)) =>
      logger.info(s"Adding action to planning problem: ${a._1}")
      (a._2, addAction(a._1))
    }
  }.toMap

  /** */
  val inititalPlanState =
    (for (userDomainEntity <- spec._1 if userDomainEntity.inPlanningDomain)
      yield userDomainEntity.planningDomainRepresentation(this)).flatten.map { v =>
      addVariable(v._1)
      (v._1, v._2)
    } ++ declaredActionsList.flatMap(_.initialState)

  override val initialState = constructState(inititalPlanState)

  //Set up goal state
  override val goalState = configureHypothesis(spec._3)

  /** Maps a variable name to its corresponding function. */
  lazy val functionByName = variablesByName.toMap

  /** For each variable/action, corresponding name in the planning domain. */
  private[this] val entityNames = scala.collection.mutable.Map[Any, String]()

  private[this] val variablesByName = scala.collection.mutable.Map[String, PlanningDomainFunction]()

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

  //TODO: Resolve fixation on 'loadedModel'
  protected def convertModelRelation(model: String, p: PredicateRelation): PlanningDomainFunction = p match {
    case alesia.query.Conjunction(l, r) => convertModelRelation(model, l) and convertModelRelation(model, r)
    case alesia.query.Disjunction(l, r) => convertModelRelation(model, l) or convertModelRelation(model, r)
    case alesia.query.Negation(r) => !convertModelRelation(model, r)
    case alesia.query.hasProperty(prop) => addVariable(s"${prop}(loadedModel)")
    case alesia.query.hasAttributeValue(a, v) => addVariable("${a}(loadedModel, ${v})")
  }

  //TODO: Generalize this
  protected def interpretHypothesisElement(h: HypothesisElement): PlanningDomainFunction = h._1 match {
    case alesia.query.exists => h._2 match {
      case alesia.query.model => convertModelRelation("loadedModel", h._3)
      case alesia.query.model(pattern) => restrictModelToPattern(pattern) and convertModelRelation("loadedModel", h._3)
      case _ => ???
    }
    case _ => ???
  }

  protected def configureHypothesis(h: UserHypothesis): PlanningDomainFunction = {
    val elems = FormulaConverter.extractHypothesisElements(h)
    conjunction(elems.map(interpretHypothesisElement))
  }

  protected def restrictModelToPattern(pattern: String): PlanningDomainFunction = ???

  protected def addVariable(s: String): PlanningDomainFunction = addVariable(PublicLiteral(s))

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

  override def constructState(xs: PlanState): PlanningDomainFunction =
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