package org.palladiosimulator.analyzer.slingshot.simulation.core.events;

import java.util.List;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public class SimulationStarted implements DESEvent {


	private String eventId;
	
	public SimulationStarted() {
		this.eventId = UUID.randomUUID().toString();
	}
	
	@Override
	public String getId() {
		return eventId;
	}

	@Override
	public List<DESEvent> handle() {
		// no side-effect
		return List.of();
	}

	@Override
	public double getDelay() {
		// TODO Auto-generated method stub
		return 0;
	}

}
