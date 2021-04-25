package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.active;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;

import de.uka.ipd.sdq.probfunction.math.util.MathTools;

/**
 * A FCFS resource (first-come first-serve) handles the {@link Job}s in the FIFO
 * order.
 * 
 * @author Julijan Katic
 */
public final class FCFSResource extends AbstractActiveResource {

	private static final Logger LOGGER = Logger.getLogger(FCFSResource.class);

	/**
	 * The FIFO queue of jobs to process.
	 */
	private final Deque<Job> processes = new ArrayDeque<>();

	/**
	 * 
	 */
	private double lastTime;

	public FCFSResource(final String id, final String name, final long capacity) {
		super(id, name, capacity);
	}

	@Override
	public void enqueue(final Job job) {
		LOGGER.info("FCFS: Process " + job + " demanding " + job.getDemand());
		this.processes.offer(job);
	}

	@Override
	public void process(final double elapsedTime) {
		if (this.isEmpty()) {
			return;
		}

		final Job finishedJob = this.processes.poll();
		this.updateInternalTimer(elapsedTime);

		assert MathTools.equalsDouble(0, finishedJob.getDemand())
				: "Remaining demand (" + finishedJob.getDemand() + ") not zero!";

		this.notify(finishedJob);
	}

	@Override
	public boolean isEmpty() {
		return this.processes.isEmpty();
	}

	@Override
	public void cancelJob(final Job job) {
		this.processes.remove(job);
	}

	private void updateInternalTimer(final double simulationTime) {
		assert MathTools.lessOrEqual(this.lastTime, simulationTime);

		final double passedTime = simulationTime - this.lastTime;
		if (MathTools.less(0, passedTime)) {
			final Job first = this.processes.peek();
			if (first != null) {
				double newDemand = first.getDemand() - passedTime;

				// avoid trouble caused by rounding issues
				newDemand = MathTools.equalsDouble(newDemand, 0) ? 0.0 : newDemand;

				assert newDemand >= 0 : "Remaining demand (" + newDemand + ") smaller than zero!";

				first.updateDemand(newDemand);
			}
		}

		this.lastTime = simulationTime;
	}

}
