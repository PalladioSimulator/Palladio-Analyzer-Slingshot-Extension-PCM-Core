package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.UserEntryRequest;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class UserEntryRequested extends AbstractEntityChangedEvent<UserEntryRequest> {

	public UserEntryRequested(final UserEntryRequest entity, final double delay) {
		super(entity, delay);
	}

}
