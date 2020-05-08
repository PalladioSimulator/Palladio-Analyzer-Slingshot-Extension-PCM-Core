package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.User;

public class UserStarted extends AbstractEntityChangedEvent<User> {


	public UserStarted(final User simulatedUser) {
		super(simulatedUser,0);
	}


}
