package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data;

import java.util.Map;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;

/**
 * This interface details out the implementation of a certain type
 * of model change that shall happen when triggering.
 * 
 * @author Julijan Katic
 *
 */
public interface AdjustmentExecutor {

	/**
	 * This method is the actual modification execution of the resource environment.
	 * This can either add or delete model elements for example.
	 * 
	 * @param triggerContext The trigger context from where the trigger happens.
	 * @return A result containing all the changes that happened.
	 */
	public AdjustmentResult onTrigger(final TriggerContext triggerContext);
	
	/**
	 * This method modifies certain adjustment values before the trigger happens.
	 * This can happen, for example, when a modifying constraint is called. 
	 * 
	 * @param valuesToModify The parameters that are needed when modifying adjustment values. 
	 * 				Can be different from implementation to implementation.
	 */
	public void modifyValues(final Map<String, Object> valuesToModify);
}
