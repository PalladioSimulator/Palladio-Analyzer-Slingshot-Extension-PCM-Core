package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.active;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.IResourceHandler;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobScheduled;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RequestFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

import de.uka.ipd.sdq.probfunction.math.util.MathTools;

/*
 * Mostly from https://github.com/PalladioSimulator/Palladio-Simulation-Scheduler/blob/master/bundles/de.uka.ipd.sdq.scheduler/src/de/uka/ipd/sdq/scheduler/resources/active/SimFCFSResource.java
 */
/**
 * An event-driven implementation of FCFS active resource where the behavior is
 * as specified in de.uka.ipd.sdq.scheduler.resources.active.SimFCFSResource
 * 
 * @author Floriment Klinaku
 */
public class FCFSResource implements IResourceHandler, IActiveResource {
	private final Logger LOGGER = Logger.getLogger(FCFSResource.class);

	/**
	 * This data holds the jobs (or active resource) that is updated event after
	 * event.
	 */
	private final Deque<Job> processQ = new ArrayDeque<>();

	/**
	 * The map mapping each job to the number of units it has acquired or worked.
	 * Unlike {@link Job#getDemand()}, this number can change throughout the
	 * simulation.
	 */
	private final Map<Job, Double> runningProcesses = new HashMap<>(); // Instead of Hashtable, use HashMap for better
	                                                                   // performance

	/**
	 * Used in the method {@link #toNow(double)} to calculate the time when a job
	 * was updated.
	 */
	private double lastTime = 0.0;

	@Override
	public ResultEvent<DESEvent> onSimulationStarted(final SimulationStarted evt) {
		// activate resources
		// we could create a tuple of next processing finished and also that a user
		// request is finished.
		// new ProcessingFinished(delay) -> next
		// new UserFinished(now)
		return ResultEvent.of();
	}

	@Override
	public ResultEvent<DESEvent> onJobInitiated(final JobInitiated evt) {

		LOGGER.info(String.format("User requests processing '%f' users for closed workload simulation", evt.time()));

		toNow(evt.time());

		// TODO:: Demand should come from the clients currently all set to one.
		final Job newJob = evt.getEntity();

		processQ.add(newJob);
		runningProcesses.put(newJob, newJob.getDemand());

		// add demand to the resource
		// new ProcessingStarted
		// it is the the one that arrived now
		if (processQ.size() == 1) {
			LOGGER.info("[User Arrival]: Single user -> we need to schedule the getNextEvent");
			return ResultEvent.of(new JobScheduled(newJob, 0), getNextEvent());
		} else {
			LOGGER.info("[User Arrival]: Multiple users exist -> wait in queue");
			return ResultEvent.of(new JobScheduled(newJob, 0));
		}
	}

	@Override
	public ResultEvent<DESEvent> onJobFinished(final JobFinished evt) {
		// the state of the resource has not changed until this point in time.
		toNow(evt.time());
		LOGGER.info(String.format("[Processing Finished]: User requests finished at '%f'", evt.time()));

		final Job job = evt.getEntity();

		assert MathTools.equalsDouble(0, runningProcesses.get(job)) : "Remaining demand (" + runningProcesses.get(job)
		        + ") not zero!";

		runningProcesses.remove(job);
		processQ.remove(job);

		final RequestFinished userFinished = null; // TODO: new RequestFinished(job.getRequest());

		return ResultEvent.of(getNextEvent(), userFinished);
	}

	@Override
	public ResultEvent<DESEvent> onJobProgressed(final JobProgressed evt) {
		return ResultEvent.empty();
	}

	private JobFinished getNextEvent() {
		final Job first = processQ.peek();
		// here we get rid of events that are scheduled on processing finished.
		// processingFinished.removeEvent(); -> no need to remove events that have been
		// scheduled to the engine
		if (first != null) {
			final double time = runningProcesses.get(first);
			return new JobFinished(first, time);
		}
		return null;
	}

	/*
	 * From SimuLizar
	 */
	/**
	 * Updates the processed demands for the first job (as this is FCFS). Each
	 * passed time is considered to be one unit of work.
	 * 
	 * @param simulationTime The current time of the simulation.
	 */
	private void toNow(final double simulationTime) {
		final double now = simulationTime;
		final double passedTime = now - this.lastTime;
		if (MathTools.less(0, passedTime)) {
			final Job first = processQ.peek();
			if (first != null) {
				double demand = runningProcesses.get(first) - passedTime;

				/* avoid trouble caused by rounding issues */
				demand = MathTools.equalsDouble(demand, 0) ? 0.0 : demand;
				assert demand >= 0 : "Remaining demand (" + demand + ") smaller than zero!";

				/* Update remained time. */
				runningProcesses.put(first, demand);
			}
		}
		this.lastTime = now;
	}
}
