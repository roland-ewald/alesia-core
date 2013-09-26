package alesia.planning.domain

import java.net.URI
import alesia.query.UserDomainEntity

/**
 * Represents a parameterized model.
 *
 * @author Roland Ewald
 */
case class ParameterizedModel(val modelURI: String, val parameters: Map[String, Any] = Map()) extends UserDomainEntity