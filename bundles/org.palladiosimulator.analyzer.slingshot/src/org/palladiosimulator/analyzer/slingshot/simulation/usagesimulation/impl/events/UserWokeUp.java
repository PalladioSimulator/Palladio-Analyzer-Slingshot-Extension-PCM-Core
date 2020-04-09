package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;

public class UserWokeUp extends AbstractEntityChangedEvent<SimulatedUser> {	
	public UserWokeUp(final SimulatedUser user, final double delay) {
		super(user,delay);
	}
}
 
