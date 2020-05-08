package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.User;

public class UserWokeUp extends AbstractEntityChangedEvent<User> {	
	public UserWokeUp(final User user, final double delay) {
		super(user,delay);
	}
}
 
