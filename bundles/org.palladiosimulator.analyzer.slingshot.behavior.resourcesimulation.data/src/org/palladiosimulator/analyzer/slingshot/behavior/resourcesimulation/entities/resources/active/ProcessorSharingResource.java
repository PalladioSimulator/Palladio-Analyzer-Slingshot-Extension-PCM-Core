package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.active;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

import de.uka.ipd.sdq.probfunction.math.util.MathTools;

/**
 * A processor sharing resource is a resource that can be shared by multiple
 * {@link Job}s. A PS resource follows the Round-Robin rule of determining the
 * next job to handle.
 * 
 * @author Julijan Katic
 *
 */
public final class ProcessorSharingResource extends AbstractActiveResource {

	private static final Logger LOGGER = Logger.getLogger(ProcessorSharingResource.class);

	/** The minimum amount of time used for scheduling an event. */
	private static final double JIFFY = 1e-9;

	/** The running jobs sorted by its demand. */
	private final SortedSet<Job> runningJobs = new TreeSet<>();

	/** The numbers of processes on each core. */
	private final List<Integer> numberProcessesOnCore;

	/** The time that is used to keep track of processes. */
	private double internalTime;

	public ProcessorSharingResource(final String id, final String name, final long capacity) {
		super(id, name, capacity);
		this.numberProcessesOnCore = new ArrayList<>((int) capacity);

		for (int i = 0; i < capacity; i++) {
			this.numberProcessesOnCore.add(0);
		}
	}

	@Override
	public void enqueue(final Job job) {
		if (job.getDemand() < JIFFY) {
			job.updateDemand(JIFFY);
			LOGGER.info(job + " demand was increased to match JIFFY");
		}

		this.runningJobs.add(job);
		this.reportCoreUsage();
	}

	@Override
	public boolean isEmpty() {
		return this.runningJobs.isEmpty();
	}

	@Override
	public void process(final double elapsedTime) {
		final Job shortestJob = this.runningJobs.first();
		if (shortestJob != null) {
			double remainingTime = shortestJob.getDemand() * this.getProcessingDelayFactorPerProcess();
			remainingTime = remainingTime < JIFFY ? 0.0 : remainingTime;

			assert remainingTime >= 0 : "Remaining time (" + remainingTime + ") smaller than zero!";

			this.notify(shortestJob);
			this.runningJobs.remove(shortestJob);
		}
	}

	@Override
	public void cancelJob(final Job job) {
		this.runningJobs.remove(job);
	}

	/**
	 * Updates the demand of each job.
	 * 
	 * @param simulationTime the new time.
	 */
	private void updateInternalTimer(final double simulationTime) {
		assert MathTools.lessOrEqual(this.internalTime, simulationTime);

		final double passedTime = simulationTime - this.internalTime;
		final double processedDemandPerThread = passedTime / this.getProcessingDelayFactorPerProcess();

		if (MathTools.less(0, passedTime)) {
			this.runningJobs.forEach(job -> {
				final double remaining = job.getDemand() - processedDemandPerThread;
				job.updateDemand(remaining);
			});
		}
	}

	/**
	 * Returns the speed of processing per core.
	 * 
	 * @return the speed of core. Always {@code >= 1.0}.
	 */
	private double getProcessingDelayFactorPerProcess() {
		final double speed = (double) this.runningJobs.size() / (double) this.getCapacity();
		return speed < 1.0 ? 1.0 : speed;
	}

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
			final int minNumberProcessAtCore = (int) Math
					.floor((double) this.runningJobs.size() / (double) this.getCapacity());
			int numberAdditionalProcesses = (int) (this.runningJobs.size()
					- (minNumberProcessAtCore * this.getCapacity()));
			int numberProcessesAtCore;

			for (int i = 0; i < this.getCapacity(); i++) {
				if (numberAdditionalProcesses > 0) {
					numberProcessesAtCore = minNumberProcessAtCore + 1;
					numberAdditionalProcesses--;
				} else {
					numberProcessesAtCore = minNumberProcessAtCore;
				}

				this.assignProcesses(numberProcessesAtCore, i);
			}
		}
	}

	private void assignProcesses(final int targetNumberProcessesAtCore, final int coreNumber) {
		if (this.numberProcessesOnCore.get(coreNumber) != targetNumberProcessesAtCore) {
			this.numberProcessesOnCore.set(coreNumber, targetNumberProcessesAtCore);
		}
	}
}
