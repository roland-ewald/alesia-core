package alesia.planning.domain

import sessl.Simulator
import alesia.query.UserDomainEntity
import alesia.planning.PlanningProblem

/**
 * Represents a (simulation) algorithm.
 *
 * @author Roland Ewald
 */
trait Algorithm[T <: sessl.Algorithm] extends UserDomainEntity {

  def entity: T

  override def inPlanningDomain = true

  override def planningDomainRepresentation(p: PlanningProblem): Seq[(String, Boolean)] = Seq((entity.toString, true))
}

/**
 * Represents a parameterized algorithm.
 */
case class ParameterizedAlgorithm[T <: sessl.Algorithm](algorithm: Algorithm[T], val parameters: Map[String, Any] = Map())