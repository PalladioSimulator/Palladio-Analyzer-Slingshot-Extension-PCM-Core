package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import java.util.List;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;

public class UserFinished implements DESEvent {
	
	private String eventId;
	private SimulatedUser simulatedUser;
	public UserFinished(final SimulatedUser simulatedUser) {
		this.eventId = UUID.randomUUID().toString();
		this.simulatedUser = simulatedUser;
	}

	@Override
	public String getId() {
		return eventId;
	}

	@Override
	public List<DESEvent> handle() {
		// the event routine of this event is empty, because when the user is finished there are no further effects on the state. 
		return List.of();
	}

	public SimulatedUser getSimulatedUser() {
		return simulatedUser;
	}

	@Override
	public double getDelay() {
		return 0;
	}


}
