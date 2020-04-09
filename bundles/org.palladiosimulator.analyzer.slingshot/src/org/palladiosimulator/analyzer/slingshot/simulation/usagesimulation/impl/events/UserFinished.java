package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import java.util.List;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;

public class UserFinished extends AbstractEntityChangedEvent<SimulatedUser> {
	
	public UserFinished(final SimulatedUser simulatedUser) {
		super(simulatedUser,0);
	}
}
