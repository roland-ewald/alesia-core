package alesia.planning.actions.experiments

import sessl.util.Logging
import alesia.bindings.ExperimentProvider

/**
 * Checks whether this model exhibits a quasi-steady state property and, if so, from which point in simulation time on.
 *
 * @author Roland Ewald
 */
case class CheckQSSModelProperty() extends ExperimentAction with Logging {

  override def execute(implicit provider: ExperimentProvider) {
    //TODO: check if last three--five points form a line??? 
  }

}