package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.analyzer.slingshot.simulation.extensions.model.SimulationModel;

/**
 * Describes the general simulation behavior.
 * 
 * @author Julijan Katic
 */
public interface Simulation {

	/**
	 * Initialize the simulation. This should be done before starting the simulation
	 * and is generally not recommended to be called afterwards.
	 * 
	 * @param model The model providing the information needed within the
	 *              simulation.
	 */
	void init(SimulationModel model) throws Exception;

	/**
	 * Starts the simulation. Should only be called after
	 * {@link #init(SimulationModel)}.
	 */
	void startSimulation();

}
