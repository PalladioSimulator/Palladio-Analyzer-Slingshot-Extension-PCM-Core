package org.palladiosimulator.analyzer.slingshot.simulation.core.events;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * This special event will be spawned indicating that the simulation has
 * started. This is a special event that cannot be spawned afterwards again.
 * 
 * @author Julijan Katic
 */
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
