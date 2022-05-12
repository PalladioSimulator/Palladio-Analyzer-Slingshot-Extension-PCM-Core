package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.events;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class AdjustmentNotMade extends AbstractEntityChangedEvent<AdjustmentResult> {

	public AdjustmentNotMade(final AdjustmentResult adjustmentResult) {
		super(adjustmentResult, 0);
		assert !adjustmentResult.isSuccess();
	}

}
