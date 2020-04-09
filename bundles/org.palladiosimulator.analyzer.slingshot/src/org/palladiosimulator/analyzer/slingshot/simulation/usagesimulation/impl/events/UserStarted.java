package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;

public class UserStarted extends AbstractEntityChangedEvent<SimulatedUser> {


	public UserStarted(final SimulatedUser simulatedUser) {
		super(simulatedUser,0);
	}


}
