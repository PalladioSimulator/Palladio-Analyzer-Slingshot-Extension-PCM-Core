package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.active;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.IResourceHandler;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

import com.google.common.base.Preconditions;

import de.uka.ipd.sdq.probfunction.math.util.MathTools;

/**
 * An event-driven implemenation of ProcessorSharingResource where the behavior
 * is as specified in de.uka.ipd.sdq.scheduler.resources.active.ProcessorSharing
 * 
 * @author Floriment Klinaku
 *
 */
public class ProcessorSharingResource implements IResourceHandler, IActiveResource {

	private final Logger LOGGER = Logger.getLogger(ProcessorSharingResource.class);

	/**
	 * The minimum amount of time used for scheduling an event
	 */
	private final static double JIFFY = 1e-9;

	// state
	/**
	 * The map mapping each job to the number of units it has acquired or worked.
	 * Unlike {@link Job#getDemand()}, this number can change throughout the
	 * simulation.
	 */
	private final Map<Job, Double> runningProcesses = new HashMap<>();

	/**
	 * Used in the method {@link #toNow(double)} to calculate the time when a job
	 * was updated.
	 */
	private double lastTime = 0.0;

	/**
	 * Keeps track of the current number of processes assigned to each core.
	 */
	private final List<Integer> numberProcessesOnCore;

	/** @see #getCapacity */
	private final long capacity;

	/**
	 * Mutable UUID that indicates that identifies the state of this instance. That
	 * is, if the state changes, then this id will be reset.
	 */
	private UUID currentState;

	/**
	 * Instantiates a new resource that adheres to the processor sharing scheduling
	 * strategy (a.k.a. "Round Robin").
	 * 
	 * @param name     The name of the resource it has.
	 * @param id       The id of the resource. Two resources are considered equal if
	 *                 they have the same id.
	 * @param capacity The number resources it has.
	 */
	public ProcessorSharingResource(final String name, final String id, final long capacity) {
		Preconditions.checkArgument(capacity >= 0, "The capacity must be a positive number or 0.");

		this.capacity = capacity;
		this.numberProcessesOnCore = new ArrayList<Integer>((int) getCapacity());
		for (int i = 0; i < getCapacity(); i++) {
			numberProcessesOnCore.add(0);
		}

		currentState = UUID.randomUUID();
	}

	/**
	 * The number of cores.
	 * 
	 * @return the number of cores.
	 */
	public long getCapacity() {
		return capacity;
	}

	@Override
	public ResultEvent<DESEvent> onSimulationStarted(final SimulationStarted evt) {
		return ResultEvent.empty();
	}

	@Override
	public ResultEvent<DESEvent> onJobInitiated(final JobInitiated evt) {
		toNow(evt.time());

		final Job newJob = evt.getEntity();

		double demand = newJob.getDemand();

		if (demand < JIFFY) {
			demand = JIFFY;
			LOGGER.info("PS: " + newJob + " demand was increased to match JIFFY " + demand);
		}

		LOGGER.info("PS: " + newJob + " demands " + demand);

		runningProcesses.put(newJob, demand);
//		TODO:: Check reportCoreUsage
//		reportCoreUsage();
		final JobProgressed jobProgressed = scheduleNextEvent();

		if (jobProgressed != null) {
			return ResultEvent.of(jobProgressed);
		} else {
			return ResultEvent.of();
		}
	}

	@Override
	public ResultEvent<DESEvent> onJobFinished(final JobFinished evt) {
		return ResultEvent.of(); // TODO: new RequestFinished(evt.getEntity().getRequest()));
	}

	@Override
	public ResultEvent<DESEvent> onJobProgressed(final JobProgressed evt) {

		if (currentState.compareTo(evt.getExpectedResourceState()) != 0) {
			LOGGER.info("State of passive resource has changed, waiting for next JobProgressed");

			return ResultEvent.empty();
		}

		toNow(evt.time());

		final Job shortestJob = evt.getEntity();

		runningProcesses.remove(shortestJob);

		reportCoreUsage();

		LOGGER.info("Job finished " + evt.getExpectedResourceState());

		final JobFinished jobFinishedEvt = new JobFinished(shortestJob, 0);
		final JobProgressed jobProgressed = scheduleNextEvent();

		if (jobProgressed == null) {
			return ResultEvent.of(jobFinishedEvt);
		} else {
			return ResultEvent.of(jobFinishedEvt, scheduleNextEvent());
		}
	}

	/**
	 * Returns a new event for the currently shortest job.
	 * 
	 * @return
	 */
	private JobProgressed scheduleNextEvent() {
		// potentially this shortestjob will finish at the remainingTime.
		// in case a new job arrives that is shorter than this will be scheduled earlier
		// then we will have JobProgressed at time t,
		// JobProgressed at time t+delta should be invalidated somehow.

		/* The return value */
		JobProgressed nextEvent = null;

		currentState = UUID.randomUUID();

		Job shortestJob = null;
		for (final Job job : runningProcesses.keySet()) {
			if (shortestJob == null || runningProcesses.get(shortestJob) > runningProcesses.get(job)) {
				shortestJob = job;
			}
		}

		if (shortestJob != null) {
			double remainingTime = runningProcesses.get(shortestJob) * getProcessingDelayFactorPerJob();

			// avoid trouble caused by rounding issues
			remainingTime = MathTools.less(remainingTime, JIFFY) ? 0.0 : remainingTime;

			assert remainingTime >= 0 : "Remaining time (" + remainingTime + ") smaller than zero!";

			nextEvent = new JobProgressed(shortestJob, remainingTime, currentState);
		}

		return nextEvent;
	}

	/**
	 * Updates the simulation time of {@link lastTime} to {@code simulationTime} and
	 * also updates for each job the remaining time that still has to be processed.
	 * 
	 * @param simulationTime The current simulation time.
	 */
	private void toNow(final double simulationTime) {
		final double now = simulationTime;
		final double passedTime = now - this.lastTime;

		final double processedDemandPerJob = passedTime / getProcessingDelayFactorPerJob();

		if (MathTools.less(0, passedTime)) {
			for (final Entry<Job, Double> e : runningProcesses.entrySet()) {
				final double rem = e.getValue() - processedDemandPerJob;
				e.setValue(rem);
			}
		}

		this.lastTime = now;
	}

	/**
	 * Returns the delay factor per job.
	 * 
	 * @return delay factor per job.
	 */
	private double getProcessingDelayFactorPerJob() {
		final double speed = (double) runningProcesses.size() / (double) getCapacity();
		return speed < 1.0 ? 1.0 : speed;
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
