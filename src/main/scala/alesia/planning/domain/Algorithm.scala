package alesia.planning.domain

import sessl.Simulator

/**
 * Representation of algorithms in ALeSiA.
 *
 * @author Roland Ewald
 */
case class Algorithm[T <: sessl.Algorithm](val entity: T)