package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.active;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

/**
 * The super type of any active resource that exists. It processes {@link Job}s
 * with the required method.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractActiveResource implements ActiveResource {

	public static final int INFINITE_CAPACITY = -1;

	/** the unique identifier of this resource. */
	private final String id;

	/** The name of this resource (does not have to be unique). */
	private final String name;

	/** The capacity of this resource. */
	private final long capacity;

	/** The list of all finished jobs. */
	private final List<Job> finishedJobs;

	/** The set of listeners when a job finishes. */
	private final Set<ActiveJobFinishedListener> finishedJobListeners;

	/**
	 * Constructs the active resource.
	 * 
	 * @param id       The unique identifier of this resource.
	 * @param name     The name of this resource. This does not have to be unique.
	 * @param capacity The capacity that the resource can handle.
	 */
	public AbstractActiveResource(final String id, final String name, final long capacity) {
		this.id = id;
		this.name = name;
		this.capacity = capacity;
		this.finishedJobs = new ArrayList<>();
		this.finishedJobListeners = new HashSet<>();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return the capacity
	 */
	public long getCapacity() {
		return this.capacity;
	}

	protected void notify(final Job job) {
		this.finishedJobListeners.forEach(listener -> listener.notify(job));
	}

	protected void notifyEveryJob() {
		this.finishedJobListeners.forEach(listener -> {
			this.finishedJobs.forEach(job -> {
				listener.notify(job);
				this.finishedJobs.remove(job);
			});
		});
	}

	protected final void finishJob(final Job job) {
		this.finishedJobs.add(job);
	}

	@Override
	public Iterator<Job> finishedJobs() {
		return this.finishedJobs.iterator();
	}

	@Override
	public void registerListener(final ActiveJobFinishedListener listener) {
		this.finishedJobListeners.add(listener);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final AbstractActiveResource other = (AbstractActiveResource) obj;
		return Objects.equals(this.id, other.id);
	}

}
