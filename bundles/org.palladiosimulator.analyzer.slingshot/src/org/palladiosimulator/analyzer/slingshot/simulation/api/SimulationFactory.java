package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineSSJ;

/**
 * This factory class provides methods in order to construct a simulator.
 * 
 * @author Julijan Katic
 */
public class SimulationFactory {

	/**
	 * This creates a simulation by injecting every behavior provider (that is
	 * extended by the Behavior Extension Point) and constructing a simulation
	 * engine.
	 */
	public static Simulation createSimulation() {

		final SimulationEngine simEngine = new SimulationEngineSSJ();

		final SimulationDriver simulationDriver = new SimulationDriver(simEngine);

		return simulationDriver;
	}

}
