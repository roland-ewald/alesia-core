package alesia.planning.domain

import java.net.URI
import alesia.query.UserDomainEntity

/**
 * Represents a parameterized model.
 */
case class ParameterizedModel(val modelURI: String, val parameters: Map[String, Any] = Map()) extends UserDomainEntity