package org.palladiosimulator.analyzer.slingshot.simulation.events;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;

public class StartUserEvent implements DESEvent {
	
	private String eventId;
	private SimulatedUser simulatedUser;
	
	public StartUserEvent(final SimulatedUser simulatedUser) {
		this.eventId = UUID.randomUUID().toString();
		this.simulatedUser = simulatedUser;
	}

	@Override
	public String getId() {
		return eventId;
	}

	@Override
	public void handle() {
		// TODO Auto-generated method stub
		// notify listener of simualted user to identify and schedule the next event
		simulatedUser.nextEvent();
	}

}
