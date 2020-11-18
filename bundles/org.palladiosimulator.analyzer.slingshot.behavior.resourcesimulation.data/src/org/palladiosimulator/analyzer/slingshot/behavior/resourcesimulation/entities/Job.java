package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.Request;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.ResourceDemandRequest;

import com.google.common.base.Preconditions;

/**
 * A {@link Job} represents an active resource job that either has to be
 * processed, is being processed or was already processed by the simulator.
 * 
 * @author Julijan Katic
 */
public class Job {

	private final String id;
	private final int priority;
	private final Job rootProcess;
	private final boolean finished;
	private double demand;
	private final ResourceDemandRequest request;

	/**
	 * General constructor instantiating a job. {@code priority} and {@code demand}
	 * must be greater or equal to 0. {@code request} must not be null.
	 * 
	 * @param priority    The priority for the job. Lower number means higher
	 *                    priority.
	 * @param rootProcess The process or job causing this job.
	 * @param finished    Initial value of whether the job is considered finished.
	 * @param demand      The number of units requested for the resource.
	 * @param request     The request object that requests this job.
	 * @see #Job(int, double, Request)
	 */
	public Job(final int priority, final Job rootProcess, final boolean finished, final double demand,
	        final ResourceDemandRequest request) {
		super();
		this.priority = priority;
		this.rootProcess = rootProcess;
		this.finished = finished;
		this.demand = demand;
		this.request = request;

		this.id = UUID.randomUUID().toString();
	}

	/**
	 * Convenient constructor that constructs an unfinished ({@link #isFinished()}
	 * returns false) job without a rootProcess (that is, {@link #getRootProcess()}
	 * will be {@code null}).
	 * 
	 * @param priority The priority for the job. Lower number means higher priority.
	 * @param demand   The number of units requested for the resource.
	 * @param request  The request object requesting this job.
	 * @see #Job(int, Job, boolean, double, Request)
	 */
	public Job(final int priority, final double demand, final ResourceDemandRequest request) {
		this(priority, null, false, demand, request);
	}

	/**
	 * The demand of the job specifying how many units of the resource is needed for
	 * this job. This is always a positive number or 0.
	 * 
	 * @return The demand of the job.
	 */
	public double getDemand() {
		return demand;
	}

	/**
	 * Sets the demand for this job. The demand must be a positive number or 0.
	 * 
	 * @param demand positive number or 0 specifying the number of units of a
	 *               resource needed.
	 * @see #getDemand()
	 */
	public void setDemand(final double demand) {
		Preconditions.checkArgument(demand >= 0, "The demand must be a positive number or 0");
		this.demand = demand;
	}

	/**
	 * The ID of this job, typically a generated UUID.
	 * 
	 * @return ID of this job.
	 */
	public String getId() {
		return id;
	}

	/**
	 * The priority for this job. Low number indicate highest priority, and 0 is the
	 * lowest possible number.
	 * 
	 * @return
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * The job that caused this job. If such root job does not exist, then null is
	 * returned.
	 * 
	 * @return The root process of this job. Returns {@code null} if this doesn't
	 *         exist.
	 */
	public Job getRootProcess() {
		return rootProcess;
	}

	/**
	 * Specifies whether the job is finished or not. After it has been finished, the
	 * job cannot be re-initiated again.
	 * 
	 * @return true iff the job is finished. False otherwise.
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * The request that requested this job.
	 * 
	 * @return Non-null request for this job.
	 */
	public ResourceDemandRequest getRequest() {
		return request;
	}

}
