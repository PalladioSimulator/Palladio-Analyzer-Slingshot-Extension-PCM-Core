package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.UserRequest;

public class UserRequestFinished extends AbstractEntityChangedEvent<UserRequest> {

private final UserInterpretationContext userContext;
	
	public UserRequestFinished(UserRequest entity, UserInterpretationContext userContext) {
		super(entity, 0);
		this.userContext = userContext;
	}

	public UserInterpretationContext getUserContext() {
		return userContext;
	}

}
