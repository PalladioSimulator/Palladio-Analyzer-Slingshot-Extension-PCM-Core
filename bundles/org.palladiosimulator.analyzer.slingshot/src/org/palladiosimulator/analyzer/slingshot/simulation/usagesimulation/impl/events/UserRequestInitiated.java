package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.User;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.UserRequest;

public class UserRequestInitiated extends AbstractEntityChangedEvent<UserRequest> {
	
	private final UserInterpretationContext userContext;
	
	public UserRequestInitiated(UserRequest entity, UserInterpretationContext userContext, double delay) {
		super(entity, delay);
		this.userContext = userContext;
	}

	public UserInterpretationContext getUserContext() {
		return userContext;
	}

}
