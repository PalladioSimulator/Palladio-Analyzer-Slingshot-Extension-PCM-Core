package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.analyzer.slingshot.module.models.ModelModule;

import com.google.inject.Injector;

/**
 * Describes the general simulation behavior.
 * 
 * @author Julijan Katic
 */
public interface Simulation {

	/**
	 * Starts the simulation. Should only be called after
	 * {@link #init(SimulationModel)}.
	 */
	void startSimulation();

	/**
	 * Initialize the simulation with an {@link Injector} that is provided by the
	 * workflow for module injectors. This should be called before the simulation
	 * starts ({@link #startSimulation}). The behavior is unknown if this method is
	 * called after the simulation has already started.
	 * 
	 * @param modelInjector The injector with modules that can provide the models
	 *                      (and different data) needed during the simulation.
	 */
	void init(final ModelModule modelInjector) throws Exception;

}
