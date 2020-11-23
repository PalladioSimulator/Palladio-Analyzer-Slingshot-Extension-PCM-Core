package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class RequestInitiated extends AbstractEntityChangedEvent<GeneralEntryRequest> {

	public RequestInitiated(final GeneralEntryRequest entity, final double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
