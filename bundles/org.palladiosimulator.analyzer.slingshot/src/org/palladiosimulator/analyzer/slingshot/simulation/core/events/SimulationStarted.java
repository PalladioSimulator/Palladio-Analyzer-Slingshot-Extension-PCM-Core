package org.palladiosimulator.analyzer.slingshot.simulation.core.events;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public class SimulationStarted implements DESEvent {

	private final String eventId;

	public SimulationStarted() {
		this.eventId = UUID.randomUUID().toString();
	}

	@Override
	public String getId() {
		return eventId;
	}

	@Override
	public double getDelay() {
		return 0;
	}

	@Override
	public double time() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTime(final double time) {
		// TODO Auto-generated method stub
	}

}
