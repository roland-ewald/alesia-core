package alesia.query

/**
 * Helper type for a consistent definition of experimentation scenarios.
 *
 * @author Roland Ewald
 */
trait Scenario {

  def domain: Seq[UserDomainEntity]

  def preferences: Seq[UserPreference]

  def hypothesis: UserHypothesis

  def toProblemSpecification = (domain, preferences, hypothesis)

}