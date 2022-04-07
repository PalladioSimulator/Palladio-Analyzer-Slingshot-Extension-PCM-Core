package org.palladiosimulator.analyzer.slingshot.simulation.core.entities;

/**
 * Contains all the current information about the simulation.
 * 
 * @author Julijan Katic
 */
public interface SimulationInformation extends Comparable<SimulationInformation> {
	
	/**
	 * Returns a non-negative value of the current simulation time.
	 * 
	 * @return The current simulation time.
	 */
	public double currentSimulationTime();
	
	/**
	 * Returns the current number of processed events.
	 * @return
	 */
	public int currentNumberOfProcessedEvents();
	
	
}
