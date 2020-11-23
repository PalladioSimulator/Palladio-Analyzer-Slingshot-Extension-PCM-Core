package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs;

import java.util.Collection;

import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import com.google.common.base.Preconditions;

/**
 * The JobContext holds multiple job references for further calculations in a
 * single resource instance.
 * 
 * @param T The type of the collection that is used to handle the jobs.
 * 
 * @author Julijan Katic
 */
public abstract class JobContext<T extends Collection<Job>> implements IJobScheduler {

	private final T runningProcesses;
	private final int capacity;
	private final ProcessingResourceSpecification processingResourceSpecification;

	/** The container that holds the right resource */
	private final ResourceContainer resourceContainer;

	public JobContext(final T runningProcesses, final int capacity,
	        final ProcessingResourceSpecification processingResourceSpecification,
	        final ResourceContainer resourceContainer) {
		this.runningProcesses = runningProcesses;
		this.processingResourceSpecification = processingResourceSpecification;
		this.capacity = capacity;
		this.resourceContainer = resourceContainer;
	}

	public T getRunningProcesses() {
		return runningProcesses;
	}

	public int getCapacity() {
		return capacity;
	}

	public ProcessingResourceSpecification getProcessingResourceSpecification() {
		return processingResourceSpecification;
	}

	public boolean addJob(final Job job) {
		Preconditions.checkNotNull(job);
		return runningProcesses.add(job);
	}

	public boolean removeJob(final Job job) {
		return runningProcesses.remove(job);
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

}
