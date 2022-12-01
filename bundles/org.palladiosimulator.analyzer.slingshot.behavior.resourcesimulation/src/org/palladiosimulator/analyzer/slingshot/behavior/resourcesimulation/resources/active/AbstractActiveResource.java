package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.AbstractResource;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;

/**
 * The abstract super class of every active resource. It provides a method to
 * check whether a {@link Job} belongs to this instance of resource.
 * <p>
 * This abstract already overrides the {@link #onJobInitiated(JobInitiated)}
 * handler by checking whether the job belongs to this instance. If so, then the
 * right implementation will be used.
 * <p>
 * The active resource is identified by the {@link ProcessingResourceType} it
 * used.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractActiveResource extends AbstractResource implements ActiveResource {

	private static final Logger LOGGER = Logger.getLogger(AbstractActiveResource.class);

	/**
	 * @deprecated Use
	 *             {@link #AbstractActiveResource(ProcessingResourceType, String, long)}
	 *             for a more sophisticated constructor.
	 */
	@Deprecated
	public AbstractActiveResource(final String id, final String name, final long capacity) {
		super(capacity, name, id);
	}

	/**
	 * Constructs the active resource. The id is specified by the {@code type} (more
	 * specifically {@code type.getId()}).
	 * 
	 * @param type     The type of the resource whose id will be used as this id.
	 * @param name     The name of the resource, typically the entity name from the
	 *                 model.
	 * @param capacity The maximum capacity of the resource.
	 */
	public AbstractActiveResource(final ActiveResourceCompoundKey id, final String name, final long capacity) {
		super(capacity, name, id);
	}

	/**
	 * The delegated handler of the resource that will be processed if the job
	 * belongs to this resource according to {@link #jobBelongsToResource(Job)}
	 * implementation.
	 * 
	 * @param jobInitiated The event.
	 * @return The appropriate events.
	 */
	protected abstract ResultEvent<JobProgressed> process(final JobInitiated jobInitiated);

	/**
	 * Checks whether the job belongs to the resource. This is done by checking
	 * whether the {@link ProcessingResourceType} specified in the {@link job} and
	 * this id ({@link #getId()}) are equal.
	 * 
	 * @param job The job to check.
	 * @return true if the job belongs to this resource.
	 */
	protected boolean jobBelongsToResource(final Job job) {
		final ActiveResourceCompoundKey jobId = ActiveResourceCompoundKey.of(
				job.getAllocationContext().getResourceContainer_AllocationContext(), job.getProcessingResourceType());
		return this.getId().equals(jobId);
	}

	@Override
	public ResultEvent<JobProgressed> onJobInitiated(final JobInitiated jobInitiated) {
		if (!this.jobBelongsToResource(jobInitiated.getEntity())) {
			return ResultEvent.empty();
		}

		return this.process(jobInitiated);
	}

}
