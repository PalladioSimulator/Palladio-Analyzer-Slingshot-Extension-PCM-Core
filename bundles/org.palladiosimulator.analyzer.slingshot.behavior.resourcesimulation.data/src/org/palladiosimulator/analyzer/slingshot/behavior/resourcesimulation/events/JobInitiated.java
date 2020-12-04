package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.JobContext;

/**
 * Specifies that the job has been added to the running jobs.
 * 
 * @author Julijan Katic
 */
public class JobInitiated extends AbstractJobEvent {

	/** The job added to the running jobs. */
	private final Job addedJob;

	/**
	 * Constructs a Job Initiated.
	 * 
	 * @param entity   The context of the job.
	 * @param addedJob The job that has been added to the list.
	 * @param delay    The delay of this event.
	 */
	public JobInitiated(final JobContext<?> entity, final Job addedJob, final double delay) {
		super(entity, delay);
		this.addedJob = addedJob;
	}

	public Job getAddedJob() {
		return addedJob;
	}
}
