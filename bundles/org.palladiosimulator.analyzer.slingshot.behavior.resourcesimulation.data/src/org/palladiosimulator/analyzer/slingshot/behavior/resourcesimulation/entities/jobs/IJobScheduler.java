package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs;

/**
 * This interface retrieves the next job to process according to a certain
 * scheduling process.
 * 
 * @author Julijan Katic
 */
public interface IJobScheduler {

	/**
	 * Updates the inner simulation time and finds the next Job that needs to be
	 * processed in accordance to the scheduling strategy.
	 * 
	 * @param simulationTime The current simulation time.
	 * @return A job that needs to be scheduled. Can be null if there is no job
	 *         left.
	 */
	Job getNextJobToRun(final double simulationTime);

	/**
	 * Updates the simulation time for further calculations.
	 * 
	 * @param simulationTime The simulation time >= 0.
	 */
	void updateSimulationTime(final double simulationTime);
}
