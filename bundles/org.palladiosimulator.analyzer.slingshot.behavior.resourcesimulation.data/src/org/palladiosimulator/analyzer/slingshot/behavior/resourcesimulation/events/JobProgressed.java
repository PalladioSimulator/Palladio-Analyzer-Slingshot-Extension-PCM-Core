package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.JobContext;

public class JobProgressed extends AbstractJobEvent {

	private final UUID expectedResourceState;

	public JobProgressed(final JobContext<?> entity, final double delay, final UUID expectedResourceState) {
		super(entity, delay);
		this.expectedResourceState = expectedResourceState;
		// TODO Auto-generated constructor stub
	}

	public JobProgressed(final JobContext<?> entity, final double delay) {
		this(entity, delay, UUID.randomUUID());
	}

	public UUID getExpectedResourceState() {
		return expectedResourceState;
	}

}
