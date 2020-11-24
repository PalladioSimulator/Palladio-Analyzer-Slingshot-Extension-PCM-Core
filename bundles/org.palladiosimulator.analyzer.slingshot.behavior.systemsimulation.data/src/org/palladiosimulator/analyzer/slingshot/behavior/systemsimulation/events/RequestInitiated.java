package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

/**
 * This event is used when a general request is being done, typically for a
 * external call from a SEFF. For a user request use {@link UserEntryRequested}
 * instead.
 * 
 * @author Julijan Katic
 * @see UserEntryRequested
 */
public class RequestInitiated extends AbstractEntityChangedEvent<GeneralEntryRequest> {

	public RequestInitiated(final GeneralEntryRequest entity, final double delay) {
		super(entity, delay);
	}

}
