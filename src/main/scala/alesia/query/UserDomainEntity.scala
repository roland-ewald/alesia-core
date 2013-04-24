package alesia.query

trait UserDomainEntity {

}

case class SingleModel(val uri: String) extends UserDomainEntity

case class ModelSet(val setURI: String) extends UserDomainEntity

case class ModelDistribution(val generatorURI: String, paramBounds: Map[String, List[_]]) extends UserDomainEntity