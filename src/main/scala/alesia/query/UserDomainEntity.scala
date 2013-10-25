package alesia.query

import alesia.planning.PlanningProblem
import alesia.planning.domain.Algorithm

/** An entity of the experimentation domain (performance analysis). */
trait UserDomainEntity {

  /** Control whether this entity should be part of the [[alesia.planning.context.ExecutionContext]].*/
  def inExecutionContext: Boolean = true

  /** Control whether this entity should be part of the [[alesia.planning.PlanningDomain]]. */
  def inPlanningDomain: Boolean = false

  /**
   * Defines the representation of the domain entity in the planning domain.
   * @return list of (name, flag) tuples, where name is the name of a new state variable and flag determines whether
   *  it is true or not
   */
  def planningDomainRepresentation(p: PlanningProblem): Seq[(String, Boolean)] = Seq()
}

/** A model parameter. */
case class ModelParameter[T <: AnyVal](name: String, lower: T, step: T, upper: T)

/** A single model. */
case class SingleModel(val uri: String) extends UserDomainEntity

/** A set of models. */
case class ModelSet(val setURI: String, params: ModelParameter[_]*) extends UserDomainEntity

/** A (potentially infinite) set of parameterized models. */
case class ModelDistribution(val generatorURI: String, paramBounds: Map[String, List[_]]) extends UserDomainEntity

/** A single simulator. */
case class SingleSimulator[T <: sessl.Simulator](override val entity: T) extends Algorithm[T]