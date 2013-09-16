package alesia.planning.preparation

import scala.collection.mutable.ListBuffer
import alesia.planning.PlanningProblem
import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.AllDeclaredActions
import alesia.planning.actions.ActionRegistry
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.Literal
import alesia.planning.context.ExecutionContext
import alesia.planning.context.SimpleExecutionContext
import alesia.query.PredicateRelation
import alesia.query.PredicateSubject
import alesia.query.Quantifier
import alesia.query.UserHypothesis
import alesia.query.ProblemSpecification
import sessl.util.Logging
import alesia.planning.actions.PublicLiteral

/**
 * Default [[PlanPreparator]] implementation.
 *
 * TODO: Variable names should be mapped to elements of the context.
 *
 * @author Roland Ewald
 */
class DefaultPlanningPreparator extends PlanningPreparator with Logging {

  /** Refers to a single 'atomic' predicate relation (and to which subject and quantifier it relates). */
  type HypothesisElement = (Quantifier, PredicateSubject, PredicateRelation)

  /** For each variable/action, corresponding name in the planning domain. */
  private[this] var nameForEntity = scala.collection.mutable.Map[Any, String]()

  /** For each name in the planning domain, corresponding variable/action.*/
  private[this] var entityForName = scala.collection.mutable.Map[String, Any]()

  private[this] var variableNames = ListBuffer[String]()

  private[this] var actionNames = ListBuffer[String]()

  /** Maps actions to their specifications. so that executable actions can be created easily. */
  private[this] val actionSpecifications = scala.collection.mutable.Map[ActionDeclaration, ActionSpecification]()

  lazy val varNames = variableNames.toList

  lazy val actNames = actionNames.toList

  /** Associate a name in the planning domain with an entity (holding meta-data etc).*/
  private[this] def associateEntityWithName(a: Any, n: String) = {
    require(!entityForName.isDefinedAt(n), s"Entity names must be unique, but ${n} is associated with both ${a} and ${entityForName(n)}")
    nameForEntity(a) = n
    entityForName(n) = a
  }

  /** Adds a variable to the planning domain. */
  private[this] def addVariable(l: Literal) = {
    associateEntityWithName(l, l.name)
    variableNames += l.name
  }

  /** Adds an action to the planning domain. */
  private[this] def addAction(a: ActionDeclaration) = {
    associateEntityWithName(a, a.name)
    actionNames += a.name
  }

  override def preparePlanning(spec: ProblemSpecification): (PlanningProblem, ExecutionContext) = {

    //Retrieve suitable actions
    val allDeclaredActions = DefaultPlanningPreparator.retrieveDeclaredActions(spec)

    val declaredActions = allDeclaredActions.flatMap(_._2)
    for (actionSpec <- allDeclaredActions; declaredAction <- actionSpec._2)
      actionSpecifications(declaredAction) = actionSpec._1
    logger.info(s"\n\nDeclared actions:\n=======================\n\n${declaredActions.mkString("\n")}, for ${allDeclaredActions.size} specifications.")

    // TODO: Make custom class out of this
    val problem = new PlanningProblem() {

      // Declare variables
      val functionByName = declaredActions.flatMap(_.literals).map { lit =>
        addVariable(lit)
        (lit.name, v(lit.name))
      }

      // Declare actions 
      val actionByName = declaredActions.map { a =>
        addAction(a)
        logger.info(s"Added action to planning problem: ${a}")
      }

      val initialState = {
        val newVariables =
          (for (userDomainEntity <- spec._1 if userDomainEntity.inPlanningDomain)
            yield userDomainEntity.planningDomainRepresentation(this)).flatten

        val newVarDomainFunctions =
          for (newV <- newVariables) yield {
            addVariable(PublicLiteral(newV._1))
            val f = v(newV._1)
            if (newV._2) f else !f
          }

        if (newVarDomainFunctions.isEmpty)
          FalseVariable
        else newVarDomainFunctions.foldLeft(TrueVariable:PlanningDomainFunction)(_ and _)
      }

      val goalState = { //TODO
        val hypothesis = spec._3
        val hypothesisElements = extractHypothesisElements(hypothesis)
        FalseVariable
      }

      /** For debugging and logging. */
      lazy val detailedDescription: String = {
        val rv = new StringBuilder
        rv.append("Variables:\n")
        for (varNum <- nextStateVarNums.keySet.toList.sorted)
          rv.append(s"#$varNum: ${variableNames.get(varNum).getOrElse("Error in variable numbering")}\n")

        rv.append("\nFunctions (by name):\n")
        functionByName.foreach(entry => rv.append(s"${entry._1}: ${entry._2}\n"))
        rv.toString
      }
    }

    import scala.language.reflectiveCalls

    logger.info(s"\n\nGenerated planning problem:\n===========================\n\n${problem.detailedDescription}")
    (problem, new SimpleExecutionContext(spec._1, spec._2))
  }

  /** Extracts single hypothesis elements. */
  def extractHypothesisElements(h: UserHypothesis): Seq[HypothesisElement] =
    h.relation.atomicRelations.map((h.quantifier, h.subject, _))

}

object DefaultPlanningPreparator extends Logging {

  /** The maximal number of rounds before action creation is aborted. */
  val maxRounds = 100

  /**
   * Creates a set of actions by repeatedly querying all action specification with the current number of
   * available actions and asking them to create additional ones. If no new actions are created (a fixed point),
   * the algorithm stops.
   *
   * @param spec the problem specification
   */
  def retrieveDeclaredActions(spec: ProblemSpecification): AllDeclaredActions = {

    val actionSpecs = ActionRegistry.actionSpecifications
    val declaredActions = scala.collection.mutable.Map[ActionSpecification, Seq[ActionDeclaration]]()

    var newActionsDeclared = true
    var counter = 0
    actionSpecs.foreach(declaredActions(_) = Seq())

    while (newActionsDeclared && counter < maxRounds) {
      logger.debug(s"Round $counter of declared action retrieval")
      val currentActions = declaredActions.toMap
      val newActions = actionSpecs.map(x => (x, x.declareConcreteActions(spec, currentActions))).toMap
      for (a <- newActions if a._2 != null && a._2.nonEmpty) {
        declaredActions(a._1) = declaredActions(a._1) ++ a._2
        logger.debug(s"Adding action declaration $a._2 for specification $a._1")
      }
      newActionsDeclared = newActions.values.exists(_.nonEmpty)
      counter += 1
    }

    if (counter == maxRounds)
      logger.warn(s"Action declaration during plan preparation took the maximum number of rounds! ($maxRounds)")

    declaredActions.toMap
  }

}