package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class UserRequestFinished extends AbstractEntityChangedEvent<UserRequest> {

	private final UserInterpretationContext userContext;
	
	public UserRequestFinished(final UserRequest entity, final UserInterpretationContext userContext) {
		super(entity, 0);
		this.userContext = userContext;
	}

	public UserInterpretationContext getUserContext() {
		return userContext;
	}

}
