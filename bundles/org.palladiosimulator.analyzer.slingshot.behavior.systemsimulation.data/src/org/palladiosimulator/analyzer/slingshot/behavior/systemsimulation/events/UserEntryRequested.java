package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.UserEntryRequest;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

/**
 * Notifies the system that a user wants to call a system's provided service.
 * This typically comes from the outside, especially when a user makes a
 * EntrySystemCall action.
 * 
 * @author Julijan Katic
 */
public class UserEntryRequested extends AbstractEntityChangedEvent<UserEntryRequest> {

	public UserEntryRequested(final UserEntryRequest entity, final double delay) {
		super(entity, delay);
	}

}
