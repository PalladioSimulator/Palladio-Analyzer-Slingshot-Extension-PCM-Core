package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.FCFSJobContext;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.AbstractJobEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.IResourceHandler;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

import com.google.common.eventbus.Subscribe;

/*
 * Mostly from https://github.com/PalladioSimulator/Palladio-Simulation-Scheduler/blob/master/bundles/de.uka.ipd.sdq.scheduler/src/de/uka/ipd/sdq/scheduler/resources/active/SimFCFSResource.java
 */
/**
 * An event-driven implementation of FCFS active resource where the behavior is
 * as specified in de.uka.ipd.sdq.scheduler.resources.active.SimFCFSResource
 * 
 * @author Floriment Klinaku
 */
@OnEvent(when = JobInitiated.class, then = JobProgressed.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = JobProgressed.class, then = { JobProgressed.class,
        JobFinished.class }, cardinality = EventCardinality.MANY)
public class FCFSResource implements IResourceHandler, SimulationBehaviorExtension {
	private final Logger LOGGER = Logger.getLogger(FCFSResource.class);

	/**
	 * When a job is initialized, a possibly existing context will be used and the
	 * job is added to that context. Afterwards, it will always return
	 * {@link JobProgressed}.
	 * 
	 * @return JobProgressed event.
	 */
	@Subscribe
	public ResultEvent<DESEvent> onJobInitiated(final JobInitiated evt) {

		if (!this.isMeantForFCFS(evt)) {
			return ResultEvent.empty();
		}

		LOGGER.info(String.format("User requests processing '%f' users for closed workload simulation", evt.time()));

		final FCFSJobContext jobContext = (FCFSJobContext) evt.getEntity();

		final Job newJob = evt.getAddedJob();
		jobContext.addJob(newJob);

		// add demand to the resource
		// new ProcessingStarted
		// it is the the one that arrived now
//		if (jobContext.getCurrentJobCount() == 1) {
//			LOGGER.info("[User Arrival]: Single user -> we need to schedule the getNextEvent");
//			return ResultEvent.of(new JobProgressed(jobContext, 0), new JobFinished(newJob, newJob.getDemand()));
//		} else {
//			LOGGER.info("[User Arrival]: Multiple users exist -> wait in queue");
//			return ResultEvent.of(new JobProgressed(jobContext, 0));
//		}

		return ResultEvent.of(new JobProgressed(jobContext, 0));
	}

//	@Override
//	public ResultEvent<DESEvent> onJobFinished(final JobFinished evt) {
//		// the state of the resource has not changed until this point in time.
//		toNow(evt.time());
//		LOGGER.info(String.format("[Processing Finished]: User requests finished at '%f'", evt.time()));
//
//		final Job job = evt.getEntity();
//
//		assert MathTools.equalsDouble(0, runningProcesses.get(job)) : "Remaining demand (" + runningProcesses.get(job)
//		        + ") not zero!";
//
//		runningProcesses.remove(job);
//		processQ.remove(job);
//
//		// final RequestFinished userFinished = null; // TODO: new
//		// RequestFinished(job.getRequest());
//
//		return ResultEvent.of(getNextEvent());
//	}

	/**
	 * This event handler will look at the current job (in FCFS) and directly return
	 * a {@link JobFinished} with a delay.
	 */
	@Subscribe
	@Override
	public ResultEvent<DESEvent> onJobProgressed(final JobProgressed evt) {
		if (!this.isMeantForFCFS(evt)) {
			return ResultEvent.empty();
		}

		final Set<DESEvent> events = new HashSet<>();

		final FCFSJobContext jobContext = (FCFSJobContext) evt.getEntity();
		final Job nextJobToRun = jobContext.getNextJobToRun(evt.time());

		if (nextJobToRun != null) {
			events.add(new JobFinished(nextJobToRun, nextJobToRun.getDemand()));
		}

		if (jobContext.hasJobsLeft()) {
			events.add(new JobProgressed(jobContext, 0, evt.getExpectedResourceState()));
		}

		return ResultEvent.of(events);
	}

	private boolean isMeantForFCFS(final AbstractJobEvent jobEvent) {
		return jobEvent.getEntity() instanceof FCFSJobContext;
	}
}
