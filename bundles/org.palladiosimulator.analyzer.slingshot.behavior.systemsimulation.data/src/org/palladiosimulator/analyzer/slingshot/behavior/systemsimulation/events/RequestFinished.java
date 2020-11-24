package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.seffspecificevents.SeffInterpretationEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

/**
 * This event is used when a request of a user is finished. It contains
 * information about the request and the user.
 * 
 * @author Julijan Katic
 */
public class RequestFinished extends AbstractEntityChangedEvent<User> implements SeffInterpretationEvent {

	public RequestFinished(final User entity) {
		super(entity, 0);
	}

}
