package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.active;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

/**
 * A functional interface telling when an active job has been finished to its
 * listeners.
 * 
 * @author Julijan Katic
 *
 */
@FunctionalInterface
public interface ActiveJobFinishedListener {

	/**
	 * Notifies that a job has finished and handles it. {@link Job#isFinished()}
	 * will return {@code true}.
	 * 
	 * @param finished The non-{@code null} finished job.
	 */
	void notify(final Job finished);

}
