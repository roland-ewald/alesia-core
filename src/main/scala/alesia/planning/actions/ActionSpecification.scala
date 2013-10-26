package alesia.planning.actions

import alesia.planning.context.ExecutionContext
import alesia.query.ProblemSpecification
import alesia.planning.execution.PlanState

/**
 * An action in the planning domain can be executed multiple times, while an action
 * implementing the {@link Action} trait should be executed just once (all specifics are given in the constructor).
 *
 * This difference is resolved by the action specification, which contains all necessary meta-data required by users and
 * the planning preparation mechanism.
 *
 * @see Action
 * @see PlanningPreparator
 *
 * @author Roland Ewald
 */
trait ActionSpecification {

  /** Factory method that may adapt the execution context. */
  def createAction(a: ActionDeclaration, c: ExecutionContext): Action

  /** Short name of the action. */
  def shortName: String

  /** Default part of action names is short name without whitespace. */
  def shortActionName = shortName.replaceAll("\\s", "")

  /** Description of the action. */
  def description: String

  /**
   * Declares additional concrete actions to be specified, given the user specification and the actions already declared by different action specs.
   * Since some action specifications may need to 'react' on the declaration of other actions by declaring additional actions, this method is called
   * repeatedly and only *newly* declared actions should be returned.
   *
   * @param spec problem specification
   * @param declaredActions holds action declarations that
   * @return *newly* declared actions, if any
   */
  def declareConcreteActions(spec: ProblemSpecification, declaredActions: AllDeclaredActions): Option[Seq[ActionDeclaration]]

  /**
   * Use this in case only a single action shall be defined.
   */
  protected def singleAction(declaredActions: AllDeclaredActions)(action: ActionDeclaration): Option[Seq[ActionDeclaration]] = {
    if (declaredActions(this).nonEmpty)
      None
    else
      Some(Seq(action))
  }
}

/**
 * Effect to specify the results of an experiment action.
 */
case class ActionEffect(condition: ActionFormula = TrueFormula, add: Seq[Literal] = Seq(), del: Seq[Literal] = Seq(), nondeterministic: Boolean) {
  lazy val publicLiterals = (add ++ del).flatMap(ActionFormula.literals[PublicLiteral](_))
  lazy val privateLiterals = (add ++ del).flatMap(ActionFormula.literals[PrivateLiteral](_))
  lazy val literals = publicLiterals ++ privateLiterals
}

/** Declares an action. */
trait ActionDeclaration {

  // Properties:
  def name: String
  def variables: Seq[String] = (preCondition.literals ++ effect.flatMap(_.literals)).map(_.name)
  def preCondition: ActionFormula
  def effect: Seq[ActionEffect]
  def publicLiterals = (preCondition.publicLiterals ++ effect.flatMap(_.publicLiterals)).toSet
  def privateLiterals = (preCondition.privateLiterals ++ effect.flatMap(_.privateLiterals)).toSet
  def literals = publicLiterals ++ privateLiterals

  def initialState: PlanState

  // Handling at runtime:

  /** The [[ActionSpecification]] that created this [[ActionDeclaration]]. */
  val specification: ActionSpecification

  /** Call the corresponding [[ActionSpecification]] to create an executable action for this [[ActionDeclaration]].*/
  final def toExecutableAction(c: ExecutionContext) = specification.createAction(this, c)

  /** Resolves unique name of literal. This is necessary to access literals later on. */
  def uniqueLiteralName(n: String): String
}

/** Straight-forward action declaration. */
case class SimpleActionDeclaration(
  specification: ActionSpecification,
  name: String,
  initState: PlanState = Seq(),
  simplePreCondition: ActionFormula = TrueFormula,
  effects: Seq[ActionEffect],
  actionSpecifics: Option[Any] = None) extends ActionDeclaration {

  def uniqueLiteralName(n: String) = uniquePrivateLiterals(n)

  val myId = ActionDeclarationUtils.newId

  val uniquePrivateLiterals: Map[String, String] = {
    val publicLiterals = simplePreCondition.publicLiterals.toSet ++ effects.flatMap(_.publicLiterals)
    val privateLiterals = (simplePreCondition.privateLiterals ++ effects.flatMap(_.privateLiterals)).toSet
    privateLiterals.map { literal =>
      (literal.name, literal.name + "_private_" + myId)
    }.toMap ++ publicLiterals.map(l => (l.name, l.name)).toMap
  }

  val initialState: PlanState = initState.map(s => (uniquePrivateLiterals(s._1), s._2))

  val preCondition = rewrite(simplePreCondition)

  val effect = effects.map { e =>
    ActionEffect(rewrite(e.condition), e.add.map(rewriteLiteral), e.del.map(rewriteLiteral), e.nondeterministic)
  }

  /** Creates new formula by replacing generic private literals with literals that have unique names.*/
  def rewrite(original: ActionFormula): ActionFormula = original match {
    case l: Literal => rewriteLiteral(l)
    case c @ Conjunction(l, r) => Conjunction(rewrite(l), rewrite(r))
    case d @ Disjunction(l, r) => Disjunction(rewrite(l), rewrite(r))
    case n @ Negation(f) => Negation(rewrite(f))
    case x => x
  }

  def rewriteLiteral(l: Literal): Literal = l match {
    case pr: PrivateLiteral => PrivateLiteral(uniquePrivateLiterals(pr.name))
    case p: PublicLiteral => p
  }

}

object ActionDeclarationUtils {

  private[this] var counter = 0

  def newId = synchronized { counter += 1; counter }
}
