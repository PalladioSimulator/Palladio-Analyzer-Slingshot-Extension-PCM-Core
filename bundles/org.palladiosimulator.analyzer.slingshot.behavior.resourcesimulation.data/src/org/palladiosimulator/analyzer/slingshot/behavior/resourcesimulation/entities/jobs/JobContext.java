package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs;

import java.util.Collection;

import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import com.google.common.base.Preconditions;

/**
 * The JobContext holds multiple job references for further calculations in a
 * single resource instance.
 * 
 * @param <T> The type of the collection that is used to handle the jobs.
 * 
 * @author Julijan Katic
 */
public abstract class JobContext<T extends Collection<Job>> implements IJobScheduler {

	private final T runningProcesses;
	private final int capacity;
	private final ProcessingResourceSpecification processingResourceSpecification;

	/** The container that holds the right resource */
	private final ResourceContainer resourceContainer;

	/**
	 * Instantiates the job context.
	 * 
	 * @param runningProcesses                The list of running processes.
	 * @param capacity                        The capacity specifying how many jobs
	 *                                        can be handled.
	 * @param processingResourceSpecification The specification of the resource.
	 * @param resourceContainer               The container holding the resource.
	 */
	public JobContext(final T runningProcesses, final int capacity,
	        final ProcessingResourceSpecification processingResourceSpecification,
	        final ResourceContainer resourceContainer) {
		this.runningProcesses = runningProcesses;
		this.processingResourceSpecification = processingResourceSpecification;
		this.capacity = capacity;
		this.resourceContainer = resourceContainer;
	}

	/**
	 * Returns the collection of running processes.
	 * 
	 * @return collection of running processes.
	 */
	public T getRunningProcesses() {
		return runningProcesses;
	}

	/**
	 * Returns the capacity specifying how many jobs can be handled.
	 * 
	 * @return the capacity.
	 */
	public int getCapacity() {
		return capacity;
	}

	public ProcessingResourceSpecification getProcessingResourceSpecification() {
		return processingResourceSpecification;
	}

	/**
	 * Adds a job to the running processes.
	 * 
	 * @param job the job to add. Must not be {@code null}.
	 */
	public void addJob(final Job job) {
		Preconditions.checkNotNull(job);
		runningProcesses.add(job);
	}

	/**
	 * Removes the job from the list.
	 * 
	 * @param job The job to be removed. Must not be {@code null}/
	 */
	public void removeJob(final Job job) {
		Preconditions.checkNotNull(job);
		runningProcesses.remove(job);
	}

	/**
	 * Returns whether there are still jobs left that needs to be processed.
	 * 
	 * @return true iff there are still jobs left.
	 */
	public boolean hasJobsLeft() {
		return !runningProcesses.isEmpty();
	}

	/**
	 * Returns the number of jobs left to be processed.
	 * 
	 * @return number >= 0 of jobs that need to be processed.
	 */
	public int getCurrentJobCount() {
		return runningProcesses.size();
	}

	/**
	 * Returns the resource container that contains the right resource.
	 * 
	 * @return the resource container.
	 */
	public ResourceContainer getResourceContainer() {
		return this.resourceContainer;
	}
}
