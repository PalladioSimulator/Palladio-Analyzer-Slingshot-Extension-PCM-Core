package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

public final class ProcessorSharingJobProgressed extends JobProgressed {

	private final UUID expectedState;

	public ProcessorSharingJobProgressed(final Job shortestJob, final double delay, final UUID expectedState) {
		super(shortestJob, delay);
		this.expectedState = expectedState;
	}

	/**
	 * @return the expectedState
	 */
	public UUID getExpectedState() {
		return this.expectedState;
	}

}
