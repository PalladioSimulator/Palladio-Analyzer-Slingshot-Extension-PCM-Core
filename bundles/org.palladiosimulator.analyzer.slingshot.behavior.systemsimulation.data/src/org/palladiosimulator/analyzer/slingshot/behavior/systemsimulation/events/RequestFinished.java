package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.seffspecificevents.SeffInterpretationEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class RequestFinished extends AbstractEntityChangedEvent<User> implements SeffInterpretationEvent {

	public RequestFinished(final User entity) {
		super(entity, 0);
	}

}
