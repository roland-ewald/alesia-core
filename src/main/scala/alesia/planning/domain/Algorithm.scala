package alesia.planning.domain

import sessl.Simulator

/**
 * Represents a (simulation) algorithm.
 *
 * @author Roland Ewald
 */
case class Algorithm[T <: sessl.Algorithm](val entity: T)

/**
 * Represents a parameterized algorithm.
 */
case class ParameterizedAlgorithm[T <: sessl.Algorithm](algorithm: Algorithm[T], val parameters: Map[String, Any] = Map())