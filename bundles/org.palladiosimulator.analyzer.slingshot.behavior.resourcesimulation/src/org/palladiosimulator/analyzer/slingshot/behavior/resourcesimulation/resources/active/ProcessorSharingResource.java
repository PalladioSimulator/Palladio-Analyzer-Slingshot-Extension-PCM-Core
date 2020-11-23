package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

import com.google.common.eventbus.Subscribe;

import de.uka.ipd.sdq.probfunction.math.util.MathTools;

/**
 * An event-driven implemenation of ProcessorSharingResource where the behavior
 * is as specified in de.uka.ipd.sdq.scheduler.resources.active.ProcessorSharing
 * 
 * @author Floriment Klinaku
 *
 */
public class ProcessorSharingResource implements IResourceHandler, SimulationBehaviorExtension {

	private final Logger LOGGER = Logger.getLogger(ProcessorSharingResource.class);

	/**
	 * The minimum amount of time used for scheduling an event
	 */
	private final static double JIFFY = 1e-9;

	/**
	 * Mutable UUID that indicates that identifies the state of this instance. That
	 * is, if the state changes, then this id will be reset.
	 */
	private final UUID currentState;

	/**
	 * Instantiates a new resource that adheres to the processor sharing scheduling
	 * strategy (a.k.a. "Round Robin").
	 */
	public ProcessorSharingResource() {
		currentState = UUID.randomUUID();
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
			return ResultEvent.of(new JobProgressed(jobContext, remainingTime, currentState));
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

//			reportCoreUsage();

			resultEvent.add(new JobFinished(nextJobToRun, ProcessorSharingResource.returnAtLeastJiffy(remainingTime)));
		}

		if (processorSharingJobContext.hasJobsLeft()) {
			resultEvent.add(new JobProgressed(processorSharingJobContext, 0, currentState));
		}

		return ResultEvent.of(resultEvent);
	}

	/**
	 * Report core usage for each individual (virtual) core. The first core always
	 * has the most processes, which ensures backward-compatibility to the time when
	 * only one core was reported to be used if at least on process was running.
	 */
	private void reportCoreUsage() {
//		if (running_processes.size() < getCapacity()) {
//			for (int i = 0; i < getCapacity(); i++) {
//				if (i < running_processes.size()) {
//					// one process active on respective core
//					assignProcessesAndFireStateChange(1, i);
//				} else {
//					// no process active on respective core
//					assignProcessesAndFireStateChange(0, i);
//				}
//			}
//		} else { // distribute across cores
//			int minNumberProcessPerCore = (int) Math.floor((double) running_processes.size() / (double) getCapacity());
//			int numberAdditionalProcesses = (int) (running_processes.size()
//					- (minNumberProcessPerCore * getCapacity()));
//			int numberProcessesAtCore;
//			for (int i = 0; i < getCapacity(); i++) {
//				// distribute processes evenly across cores. The first
//				// (getCapactity()-1) cores can have one additional process
//				// beyond the minimal number of processes.
//				if (numberAdditionalProcesses > 0) {
//					numberProcessesAtCore = minNumberProcessPerCore + 1;
//					numberAdditionalProcesses--;
//				} else {
//					numberProcessesAtCore = minNumberProcessPerCore;
//				}
//				assignProcessesAndFireStateChange(numberProcessesAtCore, i);
//			}
//		}
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

	/**
	 * Assigns the provided number of processes to a core and fires a state change
	 * event if necessary.
	 * 
	 * @param targetNumberProcessesAtCore New number of active processes on the
	 *                                    core.
	 * @param coreNumber                  Number of the core, starting with 0.
	 */
	private void assignProcessesAndFireStateChange(final int targetNumberProcessesAtCore, final int coreNumber) {
//		if (!numberProcessesOnCore.get(coreNumber).equals(targetNumberProcessesAtCore)) {
//			numberProcessesOnCore.set(coreNumber, targetNumberProcessesAtCore);
//			fireStateChange(targetNumberProcessesAtCore, coreNumber);
//		}
	}
//
//	@Override
//	public double getRemainingDemand(final ISchedulableProcess process) {
//		if (!running_processes.contains(process)) {
//			return 0.0;
//		}
//		toNow();
//		return running_processes.get(process);
//	}
//	@Override
//	public void updateDemand(final ISchedulableProcess process, final double demand) {
//		boolean updated = false;
//		for (final Entry<ISchedulableProcess, Double> e : running_processes.entrySet()) {
//			if (e.getKey().equals(process)) {
//				if (Double.isNaN(demand)) {
//					if (LOGGER.isEnabledFor(Level.INFO)) {
//						LOGGER.info("Specified demand " + demand + "is not a number.");
//					}
//				}
//				e.setValue(demand);
//				updated = true;
//				break;
//			}
//		}
//		if (updated == false) {
//			throw new RuntimeException("COULD NOT UPDATE PROCESS!");
//		}
//		scheduleNextEvent();
//	}
//
//	@Override
//	protected void enqueue(final ISchedulableProcess process) {
//	}
//
//	@Override
//	public void registerProcess(final ISchedulableProcess process) {
//	}
//
//	@Override
//	public int getQueueLengthFor(final SchedulerEntity schedulerEntity, final int coreID) {
//		return numberProcessesOnCore.get(coreID);
//	}
//
//	@Override
//	public void stop() {
//	}
}
