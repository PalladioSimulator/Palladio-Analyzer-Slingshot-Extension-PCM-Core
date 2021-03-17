package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public final class SEFFExternalActionCalled extends AbstractEntityChangedEvent<GeneralEntryRequest> implements SEFFInterpreted {

	public SEFFExternalActionCalled(final GeneralEntryRequest entity) {
		super(entity, 0);
	}

}
