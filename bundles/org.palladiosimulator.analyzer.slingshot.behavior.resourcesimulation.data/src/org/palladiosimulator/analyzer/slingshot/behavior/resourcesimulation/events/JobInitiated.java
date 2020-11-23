package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.JobContext;

public class JobInitiated extends AbstractJobEvent {

	private final Job addedJob;

	public JobInitiated(final JobContext<?> entity, final Job addedJob, final double delay) {
		super(entity, delay);
		this.addedJob = addedJob;
	}

	public Job getAddedJob() {
		return addedJob;
	}
}
