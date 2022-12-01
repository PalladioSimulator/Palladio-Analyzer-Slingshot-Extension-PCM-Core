package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.AbstractJobEvent;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.ProcessorSharingJobProgressed;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

import de.uka.ipd.sdq.probfunction.math.util.MathTools;

/**
 * An active resource that adheres to the ProcessorSharing model. More
 * specifically, it adheres to the RoundRobin model. This means that each job
 * will be processed for a specific time, and then the next job will run.
 * <p>
 * This is done by using the subtracting the shortest jobs demand from each
 * other job, and directly finishing that job, since the shortest job will
 * eventually finish first in this procedure.
 * 
 * @author Julijan Katic
 */
public final class ProcessorSharingResource extends AbstractActiveResource {

	/** The shortest time (in this case: demand) the job can have. */
	private static final double JIFFY = 1e-9;

	/** The set of jobs to process, sorted by its demand. */
	private final SortedSet<Job> runningJobs;

	/** The list of cores, whose number specify the number of processes. */
	private final List<Integer> numberProcessesOnCore;

	/**
	 * The value tracking the simulation time in order to specify the elapsed time
	 * for the jobs.
	 */
	private double internalTime;

	/** The current state of the processor sharing resource. */
	private UUID currentState;

	/**
	 * Constructs a new processor sharing resource.
	 * 
	 * @param type     The processor resource type whose id will be this id.
	 * @param name     The name of the resource.
	 * @param capacity The maximum capacity of the resource.
	 */
	public ProcessorSharingResource(final ActiveResourceCompoundKey type, final String name, final long capacity) {
		super(type, name, capacity);

		this.runningJobs = new TreeSet<>();
		this.numberProcessesOnCore = new ArrayList<>((int) capacity);

		for (int i = 0; i < capacity; i++) {
			this.numberProcessesOnCore.add(0);
		}
	}

	/**
	 * Processes the event by updating the internal timer and adding the newly
	 * created job into the list of processes. Returns the {@link JobProgressed}
	 * event of the next job to process (not necessarily this job).
	 * 
	 * @return {@link JobProgressed} event of the next job.
	 */
	@Override
	protected ResultEvent<JobProgressed> process(final JobInitiated jobInitiated) {
		this.updateInternalTimer(jobInitiated.time());
		final Job newJob = jobInitiated.getEntity();

		if (newJob.getDemand() < JIFFY) {
			newJob.updateDemand(JIFFY);
		}

		this.runningJobs.add(newJob);
		this.reportCoreUsage();

		final ProcessorSharingJobProgressed jobProgressed = this.scheduleNextEvent();
		return ResultEvent.of(jobProgressed);
	}

	/**
	 * Processes the {@link JobProgressed} event by checking whether the expected
	 * state is equal to the expected event in the {@link JobProgressed} event,
	 * updating the internal timer and removing the shortest job from the list. The
	 * demand of each remaining job is decreased by that shortest demand. This
	 * results in the {@link JobFinished} event.
	 * 
	 * @return The {@link JobFinished} event of the shortest, removed job, and the
	 *         {@link JobProgressed} event of the next job to process.
	 */
	@Override
	public ResultEvent<AbstractJobEvent> onJobProgressed(final JobProgressed jobProgressed) {
		if (!(jobProgressed instanceof ProcessorSharingJobProgressed)) {
			return ResultEvent.empty();
		}

		final ProcessorSharingJobProgressed processorSharingJobProgressed = (ProcessorSharingJobProgressed) jobProgressed;
		if (processorSharingJobProgressed.getExpectedState().compareTo(this.currentState) != 0) {
			return ResultEvent.empty();
		}

		this.updateInternalTimer(jobProgressed.time());

		final Job shortestJob = processorSharingJobProgressed.getEntity();
		this.runningJobs.remove(shortestJob);
		this.reportCoreUsage();

		return ResultEvent.of(new JobFinished(shortestJob), this.scheduleNextEvent());
	}

	@Override
	public void clearJobs() {
		this.runningJobs.clear();

		for (int i = 0; i < this.getCapacity(); i++) {
			this.numberProcessesOnCore.set(i, 0);
		}
	}

	/**
	 * Returns the event holding the shortest job. The event will have a delay of
	 * the remaining time the shortest job would have to be processed. If there is
	 * no job left, {@code null} will be returned.
	 * 
	 * Furthermore, the internal state will be updated.
	 * 
	 * @return The event holding the shortest job.
	 */
	private ProcessorSharingJobProgressed scheduleNextEvent() {
		if (this.runningJobs.isEmpty()) {
			return null;
		}

		this.currentState = UUID.randomUUID();

		final Job shortestJob = this.runningJobs.first();
		double remainingTime = shortestJob.getDemand() * this.getProcessingDelayFactorPerProcess();

		/* Update remaining time to 0 if it is too small in order to avoid rounding errors. */
		remainingTime = remainingTime < JIFFY ? 0.0 : remainingTime;

		return new ProcessorSharingJobProgressed(shortestJob, remainingTime, this.currentState);
	}

	/**
	 * Updates the internal timer according to the simulationTime, and updates the
	 * demand of each remaining job.
	 * 
	 * @param simulationTime The new simulation time. Should be greater than the
	 *                       internal time.
	 */
	private void updateInternalTimer(final double simulationTime) {
		final double passedTime = simulationTime - this.internalTime;
		final double processedDemandPerThread = passedTime / this.getProcessingDelayFactorPerProcess();

		if (MathTools.less(0, passedTime)) {
			this.runningJobs.forEach(job -> {
				final double remaining = job.getDemand() - processedDemandPerThread;
				job.updateDemand(remaining);
			});
		}

		this.internalTime = simulationTime;
	}

	/**
	 * The delay factor (that is, the inverse of speed) this resource can handle.
	 * The lower the returned number, the faster the jobs can be processed. The
	 * lowest number will be {@code 1.0}, meaning that for each simulated time, that
	 * time will be decreased from the job's demand.
	 * 
	 * On the other hand, if the delay is {@code 2.0}, then for each simulatedTime
	 * only its half will be decreased from the demand, making the process longer.
	 * 
	 * @return The delay of each job to process. The shortest delay will be
	 *         {@code 1.0}.
	 */
	private double getProcessingDelayFactorPerProcess() {
		final double speed = (double) this.runningJobs.size() / (double) this.getCapacity();
		return speed < 1.0 ? 1.0 : speed;
	}

	/**
	 * Distributes the processes accordingly to the processes. If there are less
	 * jobs than the capacity, then each core will have at most one process, and the
	 * unused cores will have no process.
	 * 
	 * Otherwise, if there are more processes than the capacity allows, then each
	 * core will have roughly the same amount of processes to handle, where the
	 * first cores might have one additional job to handle then the remaining cores,
	 * depending on whether the number of running jobs is divisible by the capacity
	 * or not.
	 */
	private void reportCoreUsage() {
		if (this.runningJobs.size() < this.getCapacity()) {
			for (int i = 0; i < this.getCapacity(); i++) {
				if (i < this.runningJobs.size()) {
					this.assignProcesses(1, i);
				} else {
					this.assignProcesses(0, i);
				}
			}
		} else {
			/* Distribute across cores. */
			final int minNumberProcessesAtCore = (int) Math.floor(this.runningJobs.size() / this.getCapacity());
			int numberAdditionalProcesses = (int) (this.runningJobs.size()
					- (minNumberProcessesAtCore * this.getCapacity()));
			int numberProcessesAtCore;

			/*
			 * Because the number of jobs might not be divisible by the capacity, there will be remaining
			 * jobs that again has to be distributed accordingly. This is done by adding one job to each
			 * of the first "numberAdditionalProcesses" cores.
			 */
			for (int i = 0; i < this.getCapacity(); i++) {
				if (numberAdditionalProcesses > 0) {
					numberProcessesAtCore = minNumberProcessesAtCore + 1;
					numberAdditionalProcesses--;
				} else {
					numberProcessesAtCore = minNumberProcessesAtCore;
				}

				this.assignProcesses(numberProcessesAtCore, i);
			}

		}
	}

	/**
	 * Helper method that assigns the number of processes to the core. The
	 * coreNumber should be lower than the capacity since there are only capacity's
	 * number of cores. If the target number is already assigned to the core,
	 * nothing changes.
	 * 
	 * @param targetNumberProcessesAtCore the number to assign to the core
	 * @param coreNumber                  the core id itself.
	 */
	private void assignProcesses(final int targetNumberProcessesAtCore, final int coreNumber) {
		if (this.numberProcessesOnCore.get(coreNumber) != targetNumberProcessesAtCore) {
			this.numberProcessesOnCore.set(coreNumber, targetNumberProcessesAtCore);
		}
	}
}
