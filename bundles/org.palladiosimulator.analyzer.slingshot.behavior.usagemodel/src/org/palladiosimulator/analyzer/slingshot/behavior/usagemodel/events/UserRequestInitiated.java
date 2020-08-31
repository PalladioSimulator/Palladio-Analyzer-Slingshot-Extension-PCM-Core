package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class UserRequestInitiated extends AbstractEntityChangedEvent<UserRequest> {

	private final UserInterpretationContext userContext;
	
	public UserRequestInitiated(final UserRequest entity, final UserInterpretationContext userContext, final double delay ) {
		super(entity, delay);
		this.userContext = userContext;
	}

	public UserInterpretationContext getUserContext() {
		return userContext;
	}
}
