package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active;

import static org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality.MANY;
import static org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality.SINGLE;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.ProcessorSharingJobContext;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.AbstractJobEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.IResourceHandler;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

import com.google.common.eventbus.Subscribe;

import de.uka.ipd.sdq.probfunction.math.util.MathTools;

/**
 * An event-driven implemenation of ProcessorSharingResource where the behavior
 * is as specified in de.uka.ipd.sdq.scheduler.resources.active.ProcessorSharing
 * 
 * @author Julijan Katic
 *
 */
@OnEvent(when = JobInitiated.class, then = JobProgressed.class, cardinality = SINGLE)
@OnEvent(when = JobProgressed.class, then = { JobProgressed.class, JobFinished.class }, cardinality = MANY)
public class ProcessorSharingResource implements IResourceHandler, SimulationBehaviorExtension {

	private final Logger LOGGER = Logger.getLogger(ProcessorSharingResource.class);

	/**
	 * The minimum amount of time used for scheduling an event
	 */
	private final static double JIFFY = 1e-9;

	/**
	 * Instantiates a new resource that adheres to the processor sharing scheduling
	 * strategy (a.k.a. "Round Robin").
	 */
	public ProcessorSharingResource() {
	}

	@Subscribe
	public ResultEvent<DESEvent> onJobInitiated(final JobInitiated jobInitiatedEvent) {
		if (!this.isMeantForProcessorSharing(jobInitiatedEvent)) {
			return ResultEvent.empty();
		}

		final ProcessorSharingJobContext jobContext = (ProcessorSharingJobContext) jobInitiatedEvent.getEntity();
		final Job addedJob = jobInitiatedEvent.getAddedJob();
		final double demand = returnAtLeastJiffy(addedJob.getDemand());
		addedJob.updateDemand(demand);
		jobContext.addJob(addedJob);

		final Job currentShortestJob = jobContext.getNextPlannedEvent();

		if (currentShortestJob != null) {
			final double remainingTime = returnAtLeastJiffy(jobContext.getRemainingTimeOfJob(currentShortestJob));
			assert remainingTime >= 0 : "Remaining time (" + remainingTime + ") smaller than zero!";
			return ResultEvent.of(new JobProgressed(jobContext, remainingTime));
		} else {
			return ResultEvent.empty();
		}
	}

	/**
	 * Removes the shortest job that is currently running from the list resulting in
	 * a {@link JobFinished} event with a delay. If there are still jobs left, then
	 * {@link JobProgressed} will be scheduled.
	 */
	@Subscribe
	@Override
	public ResultEvent<DESEvent> onJobProgressed(final JobProgressed jobProgressedEvent) {
		if (!this.isMeantForProcessorSharing(jobProgressedEvent)) {
			return ResultEvent.empty();
		}

		/* Return Value */
		final Set<DESEvent> resultEvent = new HashSet<>();

		final ProcessorSharingJobContext processorSharingJobContext = (ProcessorSharingJobContext) jobProgressedEvent
		        .getEntity();
		final Job nextJobToRun = processorSharingJobContext.getNextJobToRun(jobProgressedEvent.time());

		if (nextJobToRun != null) {
			processorSharingJobContext.removeJob(nextJobToRun);
			final double remainingTime = processorSharingJobContext.getRemainingTimeOfJob(nextJobToRun);

			resultEvent.add(new JobFinished(nextJobToRun, ProcessorSharingResource.returnAtLeastJiffy(remainingTime)));
		}

		if (processorSharingJobContext.hasJobsLeft()) {
			resultEvent.add(new JobProgressed(processorSharingJobContext, 0));
		}

		return ResultEvent.of(resultEvent);
	}

	/**
	 * Checks whether the event contains an entity of type
	 * {@link ProcessorSharingJobContext}.
	 * 
	 * @param abstractJobEvent the event to check.
	 * @return true iff it is a subtype of {@link ProcessorSharingJobContext}
	 */
	private boolean isMeantForProcessorSharing(final AbstractJobEvent abstractJobEvent) {
		return abstractJobEvent.getEntity() instanceof ProcessorSharingJobContext;
	}

	/**
	 * Returns either the {@code actual} value, or if less than {@link JIFFY}, then
	 * {@link JIFFY}.
	 * 
	 * @param actual The actual value to be returned if greater than JIFFY.
	 * @return actual, or JIFFY if {@code actual < JIFFY}.
	 */
	private static double returnAtLeastJiffy(final double actual) {
		if (MathTools.less(actual, JIFFY)) {
			return JIFFY;
		} else {
			return actual;
		}
	}

}
