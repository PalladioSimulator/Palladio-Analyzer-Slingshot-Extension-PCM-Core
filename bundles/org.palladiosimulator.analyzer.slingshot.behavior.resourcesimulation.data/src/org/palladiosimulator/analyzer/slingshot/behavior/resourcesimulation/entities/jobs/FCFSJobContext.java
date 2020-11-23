package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs;

import java.util.ArrayDeque;
import java.util.Deque;

import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

import de.uka.ipd.sdq.probfunction.math.util.MathTools;

public class FCFSJobContext extends JobContext<Deque<Job>> {

	private double lastTimeSimulationTimeUpdated = 0;

	public FCFSJobContext(final int capacity,
	        final ProcessingResourceSpecification processingResourceSpecification,
	        final ResourceContainer resourceContainer) {
		super(new ArrayDeque<>(), capacity, processingResourceSpecification, resourceContainer);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Job getNextJobToRun(final double simulationTime) {
		final double passedTime = simulationTime - lastTimeSimulationTimeUpdated;

		if (MathTools.less(0, passedTime)) {
			final Job first = this.getRunningProcesses().peek();
			if (first != null) {
				double demand = first.getDemand();
				demand -= passedTime;
				demand = MathTools.equalsDouble(demand, 0) ? 0.0 : demand;
				assert demand >= 0 : "Remaining demand (" + demand + ") smaller than zero!";
				first.updateDemand(demand);
			}
			return first;
		}

		return null;
	}

	@Override
	public void updateSimulationTime(final double simulationTime) {
		this.lastTimeSimulationTimeUpdated = simulationTime;
	}

	public Job getNextPlannedJob() {
		return this.getRunningProcesses().peek();
	}
}
