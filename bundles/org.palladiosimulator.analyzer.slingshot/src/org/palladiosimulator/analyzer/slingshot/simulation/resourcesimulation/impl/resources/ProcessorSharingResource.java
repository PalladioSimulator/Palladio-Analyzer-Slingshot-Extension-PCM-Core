package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.resources;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.Job;
import org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl.events.RequestFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserStarted;

import de.uka.ipd.sdq.probfunction.math.util.MathTools;


/**
 * An event-driven implemenation of ProcessorSharingResource where the behavior is as specified in 
 * de.uka.ipd.sdq.scheduler.resources.active.ProcessorSharing
 * 
 * @author Floriment Klinaku
 *
 */
public class ProcessorSharingResource implements IResource {

	private final Logger LOGGER = Logger.getLogger(ProcessorSharingResource.class);

	/**
	 * The minimum amount of time used for scheduling an event
	 */
	static double JIFFY = 1e-9;

	// state
//	private final ProcessingFinishedEvent processingFinished; -> To be delted
	private final Hashtable<Job, Double> running_processes = new Hashtable<Job, Double>();
	private double last_time;
	/** Keeps track of the current number of processes assigned to each core. */
	private final List<Integer> numberProcessesOnCore;
	private long capacity;

	UUID currentState;

	public ProcessorSharingResource(final String name, final String id, final long capacity) {
		this.capacity = capacity;
		this.numberProcessesOnCore = new ArrayList<Integer>((int) getCapacity());
		for (int i = 0; i < getCapacity(); i++) {
			numberProcessesOnCore.add(0);
		}
		currentState = UUID.randomUUID();
	}

	public long getCapacity() {
		return capacity;
	}

	@Override
	public ResultEvent<DESEvent> onSimulationStarted(SimulationStarted evt) {
		return new ResultEvent<DESEvent>(Set.of());
	}

	@Override
	public ResultEvent<DESEvent> onJobInitiated(JobInitiated evt) {
		toNow(evt.time());

		// TODO:: this needs to come from evt, UserStarted evt.
		Job newJob = evt.getEntity();

		double demand = newJob.getDemand();

		if (demand < JIFFY) {
			demand = JIFFY;
			LOGGER.info("PS: " + newJob + " demand was increased to match JIFFY " + demand);
		}

		LOGGER.info("PS: " + newJob + " demands " + demand);

		running_processes.put(newJob, demand);
//		TODO:: Check reportCoreUsage
//		reportCoreUsage();
		JobProgressed jobProgressed = scheduleNextEvent();

		return new ResultEvent<DESEvent>(Set.of(jobProgressed));
	}

	@Override
	public ResultEvent<DESEvent> onJobFinished(JobFinished jobFinishedEvt) {
		return new ResultEvent<DESEvent>(Set.of(new RequestFinished(jobFinishedEvt.getEntity().getRequest())));
	}

	@Override
	public ResultEvent<DESEvent> onJobProgressed(JobProgressed jobProgressedEvt) {

		if (currentState.compareTo(jobProgressedEvt.getExpectedResourceState()) != 0) {
			LOGGER.info("State of passive resource has changed, waiting for next JobProgressed");

			// in case they are not equal ignore this
			return new ResultEvent<DESEvent>(Set.of());
		}

		toNow(jobProgressedEvt.time());

		Job shortestJob = jobProgressedEvt.getEntity();

		running_processes.remove(shortestJob);

		reportCoreUsage();

		LOGGER.info("Job Finished " + jobProgressedEvt.getExpectedResourceState());
		
		
		JobFinished jobFinishedEvt = new JobFinished(shortestJob, 0);
		
		
		Set<DESEvent> events = Set.of(jobFinishedEvt, scheduleNextEvent());
		
		return new ResultEvent<DESEvent>(events);
	}

	private JobProgressed scheduleNextEvent() {
		// potentially this shortestjob will finish at the remainingTime.
		// in case a new job arrives that is shorter than this will be scheduled earlier
		// then we will have JobProgressed at time t,
		// JobProgressed at time t+delta should be invalidated somehow.

		currentState = UUID.randomUUID();

		Job shortestJob = null;
		for (final Job job : running_processes.keySet()) {
			if (shortestJob == null || running_processes.get(shortestJob) > running_processes.get(job)) {
				shortestJob = job;
			}
		}
//		processingFinished.removeEvent(); ->  no need in the new world
		if (shortestJob != null) {
			double remainingTime = running_processes.get(shortestJob) * getProcessingDelayFactorPerJob();

			// avoid trouble caused by rounding issues
			remainingTime = remainingTime < JIFFY ? 0.0 : remainingTime;

			assert remainingTime >= 0 : "Remaining time (" + remainingTime + ")small than zero!";

//			processingFinished.schedule(shortest, remainingTime);

			return new JobProgressed(shortestJob, remainingTime, currentState);
		}
		return null;
	}

	private void toNow(final double simulationTime) {
		final double now = simulationTime;
		final double passed_time = now - last_time;

		double processedDemandPerJob = passed_time / getProcessingDelayFactorPerJob();

		if (MathTools.less(0, passed_time)) {
			for (final Entry<Job, Double> e : running_processes.entrySet()) {
				final double rem = e.getValue() - processedDemandPerJob;
				e.setValue(rem);
			}
		}
		last_time = now;
	}

	private double getProcessingDelayFactorPerJob() {
		final double speed = (double) running_processes.size() / (double) getCapacity();
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
	private void assignProcessesAndFireStateChange(int targetNumberProcessesAtCore, int coreNumber) {
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
