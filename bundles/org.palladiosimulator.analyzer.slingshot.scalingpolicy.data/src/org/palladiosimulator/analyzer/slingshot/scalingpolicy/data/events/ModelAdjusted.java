package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.events;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public final class ModelAdjusted extends AbstractEntityChangedEvent<AdjustmentResult> {

	public ModelAdjusted(final AdjustmentResult entity) {
		super(entity, 0);
	}

}
