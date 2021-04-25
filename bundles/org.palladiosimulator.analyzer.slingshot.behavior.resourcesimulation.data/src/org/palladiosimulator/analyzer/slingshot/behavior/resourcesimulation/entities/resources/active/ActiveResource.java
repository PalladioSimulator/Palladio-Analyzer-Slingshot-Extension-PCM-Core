package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.active;

import java.util.Iterator;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

/**
 * An active resource is a resource that processes {@link Job}s and notifies
 * when a job has finished. For example, an active resource can be a CPU,
 * Ethernet connections, etc.
 * <p>
 * With regards to listeners, this interface can either actively notify a
 * listener that a job has finished, or return an iterator of finished jobs.
 * 
 * 
 * @author Julijan Katic
 *
 */
public interface ActiveResource {

	String getId();

	String getName();

	long getCapacity();

	/**
	 * Enqueues a job into the resource to be processed. Depending on the
	 * implementation, a job can be added twice.
	 * 
	 * @param job The non-{@code null} job to add.
	 */
	void enqueue(final Job job);

	/**
	 * Returns whether there are any jobs left on this resource to process.
	 * 
	 * @return true iff there are any jobs left.
	 */
	boolean isEmpty();

	/**
	 * Processes the jobs according to the implementation.
	 * 
	 * @param elapsedTime The time that has elapsed since the last processing.
	 */
	void process(final double elapsedTime);

	/**
	 * Returns an iterator of jobs that have no demands left.
	 * 
	 * @return A non-{@code null} iterator of finished jobs.
	 */
	Iterator<Job> finishedJobs();

	/**
	 * Registers a new listener to notify when a Job has finished.
	 * 
	 * @param listener notify that a Job has finished.
	 */
	void registerListener(ActiveJobFinishedListener listener);

	/**
	 * Cancels a job and the resource stops processing it.
	 * 
	 * @param job The job to cancel.
	 */
	void cancelJob(final Job job);
}
