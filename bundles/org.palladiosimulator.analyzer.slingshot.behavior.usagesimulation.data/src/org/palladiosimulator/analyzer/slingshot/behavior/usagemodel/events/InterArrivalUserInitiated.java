package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEvent;

public final class InterArrivalUserInitiated extends AbstractEvent {

	public InterArrivalUserInitiated(final double delay) {
		super(delay);
	}
	
}
