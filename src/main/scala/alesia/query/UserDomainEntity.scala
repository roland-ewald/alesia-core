package alesia.query

import alesia.planning.PlanningProblem

/** An entity of the experimentation domain (performance analysis). */
trait UserDomainEntity {

  /** Control whether this entity should be part of the [[alesia.planning.context.ExecutionContext]].*/
  def inExecutionContext: Boolean = true

  /** Control whether this entity should be part of the [[alesia.planning.PlanningDomain]]. */
  def inPlanningDomain: Boolean = false

  /**
   * Defines the representation of the domain entity in the planning domain.
   *  @default empty
   *  @return list of (name, flag) tuples, where name is the name of a new state variable and flag determines whether
   *  it is true or not
   */
  def planningDomainRepresentation(p: PlanningProblem): Seq[(String, Boolean)] = Seq()
}

/** A single model. */
case class SingleModel(val uri: String) extends UserDomainEntity

/** A set of models. */
case class ModelSet(val setURI: String) extends UserDomainEntity

/** A (potentially infinite) set of parameterized models. */
case class ModelDistribution(val generatorURI: String, paramBounds: Map[String, List[_]]) extends UserDomainEntity