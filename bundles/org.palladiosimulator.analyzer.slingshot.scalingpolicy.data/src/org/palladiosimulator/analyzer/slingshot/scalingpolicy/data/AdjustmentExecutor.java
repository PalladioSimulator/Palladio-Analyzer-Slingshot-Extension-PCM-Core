package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;

/**
 * This interface details out the implementation of a certain type
 * of change.
 * 
 * @author Julijan Katic
 *
 */
@FunctionalInterface
public interface AdjustmentExecutor {

	/**
	 * 
	 * @param targetGroup
	 * @return
	 */
	public AdjustmentResult onTrigger(final TriggerContext targetGroup);

}
