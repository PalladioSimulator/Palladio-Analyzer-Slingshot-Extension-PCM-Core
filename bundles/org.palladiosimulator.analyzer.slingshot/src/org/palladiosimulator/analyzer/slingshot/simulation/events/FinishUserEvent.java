package org.palladiosimulator.analyzer.slingshot.simulation.events;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;

public class FinishUserEvent implements DESEvent {
	
	private String eventId;
	private SimulatedUser simulatedUser;
	public FinishUserEvent(final SimulatedUser simulatedUser) {
		this.eventId = UUID.randomUUID().toString();
		this.simulatedUser = simulatedUser;
	}

	@Override
	public String getId() {
		return eventId;
	}

	@Override
	public void handle() {
		// the event routine of this event is empty, because when the user is finished there are no further effects on the state. 
	}

	public SimulatedUser getSimulatedUser() {
		return simulatedUser;
	}


}
