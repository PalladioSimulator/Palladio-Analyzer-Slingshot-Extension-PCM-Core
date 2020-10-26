package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.Job;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class JobProgressed extends AbstractEntityChangedEvent<Job> {
	
	private final UUID expectedResourceState;
	
	public JobProgressed(final Job entity, final double delay, final UUID expectedResourceState) {
		super(entity, delay);
		this.expectedResourceState = expectedResourceState;
		// TODO Auto-generated constructor stub
	}

	public UUID getExpectedResourceState() {
		return expectedResourceState;
	}

	
}
