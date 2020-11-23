package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs;

import java.util.UUID;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.ActiveResourceRequestContext;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourcetype.ProcessingResourceType;

import com.google.common.base.Preconditions;

/**
 * A {@link Job} represents an active resource job that either has to be
 * processed, is being processed or was already processed by the simulator.
 * <p>
 * Two jobs are considered equal if their respective {@link #getId()}s are
 * equal.
 * 
 * @author Julijan Katic
 */
public class Job {

	/** The unique id of the job */
	private final String id;

	/** The current demand of this job */
	private double demand;

	/** The processing resource type that is being requested */
	private final ProcessingResourceType processingResourceType;

	private final ActiveResourceRequestContext requestContext;

	/**
	 * Initializes the Job and will generate a random id. This constructor should be
	 * used if a job is first created, i.e. when the job is requested. All fields
	 * must not be null.
	 * 
	 * @param initialDemand          The initial demand of the job. Must be >= 0.
	 * @param processingResourceType The type of the resource that needs being
	 *                               processed.
	 * @param resourceContainer      The container of the resource.
	 * @see #Job(String, double, ProcessingResourceType, ResourceContainer)
	 */
	public Job(final double initialDemand, final ProcessingResourceType processingResourceType,
	        final ActiveResourceRequestContext requestContext) {
		this(UUID.randomUUID().toString(), initialDemand, processingResourceType, requestContext);
	}

	/**
	 * Instantiates a Job with a given id and the necessary parameters for the
	 * processors to know. This constructor is typically used to copy a job, meaning
	 * that the ID is known and "replaces" the job.
	 * 
	 * @param id                     The id of the job.
	 * @param initialDemand          The initial demand that needs to be processed.
	 *                               Must be >= 0.
	 * @param processingResourceType The type of the resource that needs being
	 *                               processed.
	 */
	public Job(final String id, final double initialDemand, final ProcessingResourceType processingResourceType,
	        final ActiveResourceRequestContext requestContext) {
		this.id = Preconditions.checkNotNull(id);

		Preconditions.checkArgument(initialDemand >= 0, "The demand must be a positive number.");
		this.demand = initialDemand;

		this.processingResourceType = EcoreUtil.copy(processingResourceType);
		this.requestContext = requestContext;
	}

	/**
	 * Returns the unique identifier of the job. For jobs with the same id, they are
	 * considered to be the same job.
	 * 
	 * @return the id of this job.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the demand that the resource still needs to process.
	 * 
	 * @return
	 */
	public double getDemand() {
		return demand;
	}

	public ProcessingResourceType getProcessingResourceType() {
		return EcoreUtil.copy(processingResourceType);
	}

	public ActiveResourceRequestContext getRequestContext() {
		return requestContext;
	}

	/**
	 * Updates the job's demand to a new demand and returns the Job instance with
	 * the same id, but with {@code newDemand} as the demand.
	 * 
	 * @param newDemand The non-negative new demand that needs to be set in the new
	 *                  instance of the Job.
	 * @return A non-null, new instance of this Job with the same id, but with
	 *         newDemand.
	 */
	public void updateDemand(final double newDemand) {
		this.demand = newDemand;
	}

	/**
	 * Returns the hash code of {@link #getId()}.
	 * 
	 * @generated
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * Checks if two Jobs have the same {@link #getId()}.
	 * 
	 * @generated
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Job other = (Job) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("Job[%s]: %f (Demand)", this.id, this.demand);
	}
}
