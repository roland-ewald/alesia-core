package alesia.planning.context

import alesia.bindings.ExperimentProvider
import alesia.bindings.ResourceProvider
import alesia.query.UserDomainEntity
import alesia.query.UserPreference
import alesia.planning.execution.PlanState

/**
 * The current execution context of a plan. On this basis, a plan decides upon the next action(s). 
 * Contains references to intermediate results, user preferences, and (more generally) all other 
 * data that does not need to be represented on the level on which the planning sub-system operates.   
 *
 * @author Roland Ewald
 *
 */
trait ExecutionContext {

  /**
   * The user preferences regarding execution.
   * @return user preferences
   */
  def preferences: Seq[UserPreference]
  
  /**
   * The available domain entities.
   * @return domain entities
   */
  def entities: Seq[UserDomainEntity]
    
  
  def planState: PlanState
  
  def resources: ResourceProvider
  
  def experiments: ExperimentProvider    

}