package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

/**
 * Notifies that a general request is acknowledged and being processed.
 * 
 * @author Julijan Katic
 */
public class RequestStarted extends AbstractEntityChangedEvent<GeneralEntryRequest> {

	public RequestStarted(final GeneralEntryRequest entity, final double delay) {
		super(entity, delay);
	}

}
