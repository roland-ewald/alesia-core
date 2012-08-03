package alesia

import org.junit.runner.RunWith
import org.junit.runners.Suite.SuiteClasses
import alesia.planning.actions.TestExperimentActions
import alesia.planning.execution.actors.TestTrivialPlanExecution

/**
 * Executes all ALeSiA tests.
 * @author Roland Ewald
 */
@RunWith(value = classOf[org.junit.runners.Suite])
@SuiteClasses(value = Array(classOf[TestTrivialPlanExecution], classOf[TestExperimentActions]))
class AllTests