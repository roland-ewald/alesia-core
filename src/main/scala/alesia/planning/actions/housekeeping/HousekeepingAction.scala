package alesia.planning.actions.housekeeping

import alesia.planning.actions.Action

/**
 * Represents actions that are strictly meant for house-keeping, i.e. they do neither involve simulation runs
 * (these are represented by {@link ExperimentAction}) nor expensive numerical analysis (these are
 * represented by {@link AnalysisAction}).
 *
 * @see ExperimentAction
 * @see AnalysisAction
 *
 * @author Roland Ewald
 */
trait HousekeepingAction extends Action {

  //TODO

}