package org.palladiosimulator.analyzer.slingshot.simulation.events;

import java.util.UUID;

public class SimulationStart implements DESEvent {


	private String eventId;
	
	public SimulationStart() {
		this.eventId = UUID.randomUUID().toString();
	}
	
	@Override
	public String getId() {
		return eventId;
	}

	@Override
	public void handle() {
		// no side-effect
	}

}
