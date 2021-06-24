package org.palladiosimulator.analyzer.slingshot.simulation.api;

/**
 * Describes the general simulation behavior.
 * 
 * @author Julijan Katic
 */
//@ImplementedBy(SimulationDriver.class)
public interface Simulation {

	/**
	 * Starts the simulation. Should only be called after
	 * {@link #init(SimulationModel)}.
	 */
	void startSimulation();

	/**
	 * Initializes the simulation. This should be called before
	 * {@link #startSimulation()}. The behavior is unknown if this method is called
	 * after the simulation has been started. The implementer should further specify
	 * this behavior.
	 */
	default void init() throws Exception {
	}

	/**
	 * Stops the simulation as soon as possible.
	 */
	void stopSimulation();

}
