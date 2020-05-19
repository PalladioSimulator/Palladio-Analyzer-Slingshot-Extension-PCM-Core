package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UserInterpretationContext;

public abstract class UserChangedEvent<User> extends AbstractEntityChangedEvent<User>{

	private final UserInterpretationContext context;
	
	public UserChangedEvent(User entity, UserInterpretationContext userContext, double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
		this.context = userContext;
	}

	public UserInterpretationContext getUserInterpretationContext() {
		return context;
	}
	
	

}
