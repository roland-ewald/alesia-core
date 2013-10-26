package alesia.planning

import alesia.planning.actions.ActionDeclaration
import alesia.planning.preparation.DefaultPlanningProblem

import alesia.query._

/**
 * Dummy used for testing.
 *
 * @see [[alesia.planning.execution.TestActionSelectors]]
 *
 * @author Roland Ewald
 */
case class DummyDomainSpecificPlanningProblem(literals: String*)
  extends DefaultPlanningProblem(
    (Seq(), Seq(), DummyHypothesis), Seq()) {

  literals.foreach(addVariable)
}
