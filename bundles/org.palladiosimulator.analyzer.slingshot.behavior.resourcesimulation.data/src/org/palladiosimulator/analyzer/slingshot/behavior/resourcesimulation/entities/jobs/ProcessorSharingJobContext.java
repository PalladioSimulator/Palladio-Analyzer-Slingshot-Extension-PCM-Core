package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import de.uka.ipd.sdq.probfunction.math.util.MathTools;

public class ProcessorSharingJobContext extends JobContext<SortedSet<Job>> {

	private double lastTimeSimulationTimeUpdated = 0;

	public ProcessorSharingJobContext(final Set<Job> runningProcesses, final int capacity,
	        final ProcessingResourceSpecification processingResourceSpecification,
	        final ResourceContainer resourceContainer) {
		super(new TreeSet<>((job1, job2) -> Double.compare(job1.getDemand(), job2.getDemand())),
		        capacity, processingResourceSpecification, resourceContainer);
		this.getRunningProcesses().addAll(runningProcesses);
	}

	public ProcessorSharingJobContext(final int capacity,
	        final ProcessingResourceSpecification processingResourceSpecification,
	        final ResourceContainer resourceContainer) {
		this(Set.of(), capacity, processingResourceSpecification, resourceContainer);
	}

	@Override
	public Job getNextJobToRun(final double simulationTime) {
		final double passedTime = simulationTime - this.lastTimeSimulationTimeUpdated;
		final double updateDemandDelta = passedTime / this.getProcessingDelayFactorPerJob();

		if (MathTools.less(0, passedTime)) {
			this.getRunningProcesses().stream()
			        .forEach(job -> job.updateDemand(job.getDemand() - updateDemandDelta));
		}

		this.updateSimulationTime(simulationTime);
		return this.getNextPlannedEvent();
	}

	@Override
	public void updateSimulationTime(final double simulationTime) {
		this.lastTimeSimulationTimeUpdated = simulationTime;
	}

	public Job getNextPlannedEvent() {
		if (this.getRunningProcesses().isEmpty()) {
			return null;
		}
		return this.getRunningProcesses().first();
	}

	/**
	 * Returns the remaining time for this job.
	 * 
	 * @param job
	 * @return
	 */
	public double getRemainingTimeOfJob(final Job job) {
		return job.getDemand() * this.getProcessingDelayFactorPerJob();
	}

	/**
	 * (Helper-Method) Returns the delay factor per job.
	 * 
	 * @return delay factor per job.
	 */
	private double getProcessingDelayFactorPerJob() {
		final double speed = (double) this.getCurrentJobCount() / (double) this.getCapacity();
		return speed < 1.0 ? 1.0 : speed;
	}
}
