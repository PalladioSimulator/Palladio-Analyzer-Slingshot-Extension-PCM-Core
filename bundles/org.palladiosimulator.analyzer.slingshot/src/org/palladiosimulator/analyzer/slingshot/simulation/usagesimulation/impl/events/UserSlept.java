package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import java.util.List;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.*;

public class UserSlept extends AbstractEntityChangedEvent<SimulatedUser> {

	private final double timeToSleep;

	public UserSlept(final SimulatedUser user, final double timeToSleep) {
		super(user,0);
		this.timeToSleep = timeToSleep;
	}

}
