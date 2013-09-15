package alesia.query

/** An entity of the experimentation domain (performance analysis). */
trait UserDomainEntity

/** A single model. */
case class SingleModel(val uri: String) extends UserDomainEntity

/** A set of models. */
case class ModelSet(val setURI: String) extends UserDomainEntity

/** A (potentially infinite) set of parameterized models. */
case class ModelDistribution(val generatorURI: String, paramBounds: Map[String, List[_]]) extends UserDomainEntity