package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.Job;

public class JobProgressed extends AbstractEntityChangedEvent<Job> {
	
	private UUID expectedResourceState;
	
	public JobProgressed(Job job, double delay, UUID expectedState) {
		super(job,delay);
		this.expectedResourceState = expectedState;
	}
	
	public UUID getExpectedResourceState() {
		return expectedResourceState;
	}
}
