package alesia.planning.preparation

import alesia.planning.PlanningProblem
import alesia.planning.actions.ActionDeclaration
import alesia.planning.actions.ActionRegistry
import alesia.planning.actions.ActionSpecification
import alesia.planning.actions.Literal
import alesia.planning.actions.PublicLiteral
import alesia.planning.context.ExecutionContext
import alesia.query.PredicateRelation
import alesia.query.PredicateSubject
import alesia.query.Quantifier
import alesia.query.UserHypothesis
import alesia.query.exists
import sessl.util.Logging
import alesia.planning.actions.AllDeclaredActions
import alesia.query.ProblemSpecification
import alesia.planning.actions.ActionFormula
import alesia.planning.actions.ActionEffect
import alesia.planning.DomainSpecificPlanningProblem
import alesia.planning.context.LocalJamesExecutionContext
import scala.collection.{ mutable => mutable }

/**
 * Default [[PlanPreparator]] implementation.
 *
 * TODO: Variable names should be mapped to elements of the context.
 * TODO: Clean up, refactor, and document the preparation.
 *
 * @author Roland Ewald
 */
class DefaultPlanningPreparator extends PlanningPreparator with Logging {

  /** Refers to a single 'atomic' predicate relation (and to which subject and quantifier it relates). */
  type HypothesisElement = (Quantifier, PredicateSubject, PredicateRelation)

  /** For each variable/action, corresponding name in the planning domain. */
  private[this] var nameForEntity = mutable.Map[Any, String]()

  /** For each name in the planning domain, corresponding variable/action.*/
  private[this] var entityForName = mutable.Map[String, Any]()

  private[this] var addedVariableNames = mutable.Set[String]()

  private[this] var addedActionNames = mutable.Set[String]()

  lazy val varNames = addedVariableNames.toList

  lazy val actNames = addedActionNames.toList

  /** Associate a name in the planning domain with an entity (holding meta-data etc).*/
  private[this] def associateEntityWithName(a: Any, n: String): Unit = {
    require(!nameForEntity.isDefinedAt(a), s"Entity names must be unique, but ${a} is associated with both ${n} and ${nameForEntity(a)}")
    nameForEntity(a) = n
    entityForName(n) = a
  }

  override def preparePlanning(spec: ProblemSpecification): (DomainSpecificPlanningProblem, ExecutionContext) = {

    //Retrieve suitable actions
    val allDeclaredActions = DefaultPlanningPreparator.retrieveDeclaredActions(spec)

    val declaredActionsList = allDeclaredActions.flatMap(_._2)
    logger.info(s"\n\nDeclared actions:\n=======================\n\n${declaredActionsList.mkString("\n")}, for ${allDeclaredActions.size} specifications.")

    val preparator = this

    // TODO: new class?
    val inititalPlanState = mutable.ListBuffer[(String, Boolean)]()

    // TODO: Make custom class out of this
    // TODO: Check problems with duplicate creations of literals etc. 
    val problem = new DomainSpecificPlanningProblem() {

      private[this] var variablesByName = mutable.Map[String, PlanningDomainFunction]()

      // Declare actions
      val declaredActions = declaredActionsList.zipWithIndex.map(x => (x._2, x._1)).toMap
      val planningActions = {
        declaredActionsList.zipWithIndex map { (a: (ActionDeclaration, Int)) =>
          logger.info(s"Adding action to planning problem: ${a._1}")
          (a._2, addAction(a._1))
        }
      }.toMap

      // Set up initial state
      val initialDomainVariables =
        (for (userDomainEntity <- spec._1 if userDomainEntity.inPlanningDomain)
          yield userDomainEntity.planningDomainRepresentation(this)).flatten.map { v =>
          addVariable(v._1)
          (v._1, v._2)
        }

      inititalPlanState ++= (initialDomainVariables ++ declaredActionsList.flatMap(_.initialState))
      
      val initialState = constructState(inititalPlanState)

      //Set up goal state
      val goalState = {
        val hypothesis = spec._3
        val hypothesisElements = extractHypothesisElements(hypothesis)
        hypothesisElements.map(interpretHypothesisElement).foldLeft(TrueVariable: PlanningDomainFunction)(_ and _)
      }

      /** Maps literal names and so on to functions in the planning domain. */
      lazy val functionByName = variablesByName.toMap

      //TODO: Generalize this
      private[this] def interpretHypothesisElement(h: HypothesisElement): PlanningDomainFunction = h._1 match {
        case alesia.query.exists => h._2 match {
          case alesia.query.model => convertModelRelation("loadedModel", h._3)
          case alesia.query.model(pattern) => restrictModelToPattern(pattern) and convertModelRelation("loadedModel", h._3)
          case _ => ???
        }
        case _ => ???
      }

      //TODO: Move to dedicated converter, resolve fixation on 'loadedModel'
      private[this] def convertModelRelation(model: String, p: PredicateRelation): PlanningDomainFunction = p match {
        case alesia.query.Conjunction(l, r) => convertModelRelation(model, l) and convertModelRelation(model, r)
        case alesia.query.Disjunction(l, r) => convertModelRelation(model, l) or convertModelRelation(model, r)
        case alesia.query.Negation(r) => !convertModelRelation(model, r)
        case alesia.query.hasProperty(prop) => addVariable(s"${prop}(loadedModel)")
        case alesia.query.hasAttributeValue(a, v) => addVariable("${a}(loadedModel, ${v})")
      }

      private[this] def restrictModelToPattern(pattern: String): PlanningDomainFunction = ???

      private[this] def addVariable(s: String): PlanningDomainFunction = addVariable(PublicLiteral(s))

      /**
       * Adds a variable to the planning domain.
       *  @return true whether this is a new variable, otherwise false
       */
      private[this] def addVariable(l: Literal): PlanningDomainFunction = {
        variablesByName.getOrElseUpdate(l.name, {
          addedVariableNames += l.name
          val newVariable = v(l.name)
          associateEntityWithName(newVariable, l.name)
          newVariable
        })
      }

      private[this] def convertFormula(a: ActionFormula): PlanningDomainFunction = {
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

      /** Adds an action to the planning domain. */
      private[this] def addAction(a: ActionDeclaration): DomainAction = {

        def convertEffect(as: Seq[ActionEffect]): Seq[Effect] =
          as.map(a => Effect(convertFormula(a.condition), a.add.map(addVariable), a.del.map(addVariable), a.nondeterministic))

        addedActionNames += a.name
        val newAction = action(a.name, convertFormula(a.preCondition), convertEffect(a.effect): _*)
        associateEntityWithName(newAction, a.name)
        newAction
      }

    }

    import scala.language.reflectiveCalls

    logger.info(s"\n\nGenerated planning problem:\n===========================\n\n${problem.detailedDescription}")
    (problem, new LocalJamesExecutionContext(spec._1, spec._2, inititalPlanState.toList))
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
    val declaredActions = mutable.Map[ActionSpecification, Seq[ActionDeclaration]]()

    var newActionsDeclared = true
    var counter = 0
    actionSpecs.foreach(declaredActions(_) = Seq())

    while (newActionsDeclared && counter < maxRounds) {
      logger.debug(s"Round $counter of declared action retrieval")
      val currentActions = declaredActions.toMap
      val newActions = actionSpecs.map(x => (x, x.declareConcreteActions(spec, currentActions))).toMap
      for (a <- newActions; newDeclarations <- a._2) {
        declaredActions(a._1) = declaredActions(a._1) ++ newDeclarations
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