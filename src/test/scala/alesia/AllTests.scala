package alesia

import org.junit.runner.RunWith
import org.junit.runners.Suite.SuiteClasses
import alesia.planning.actions.TestExperimentActions

/**
 * Executes all ALeSiA tests.
 * @author Roland Ewald
 */
@RunWith(value = classOf[org.junit.runners.Suite])
@SuiteClasses(value = Array(classOf[TestExperimentActions]))
class AllTests