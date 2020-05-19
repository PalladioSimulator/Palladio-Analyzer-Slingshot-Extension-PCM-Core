package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.User;

public class UserWokeUp extends UserChangedEvent<User> {	
	public UserWokeUp(final User user, final UserInterpretationContext userContext, final double delay) {
		super(user, userContext, delay);
	}
}
 
