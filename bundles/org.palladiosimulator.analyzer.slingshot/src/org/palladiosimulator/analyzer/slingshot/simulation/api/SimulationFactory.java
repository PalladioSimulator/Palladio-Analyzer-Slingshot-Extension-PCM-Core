package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineSSJ;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.model.SimulationModel;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.model.SimulizarSimulationModel;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

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
		// final BehaviorProviderExtensionsHandler behaviorExtensionHandler = new
		// BehaviorProviderExtensionsHandler();

		// final List<DecoratedSimulationBehaviorProvider> providers =
		// behaviorExtensionHandler.getAllProviders();

		// final SimulationDriver simulationDriver = new SimulationDriver(simEngine,
		// providers);
		final SimulationDriver simulationDriver = new SimulationDriver(simEngine);

		return simulationDriver;
	}

	/**
	 * This creates a model for the simulation that contain information about the
	 * PCM models.
	 */
	public static SimulationModel createSimulizarSimulationModel(final UsageModel usageModel,
	        final Allocation allocation) {
		return new SimulizarSimulationModel(usageModel, allocation);
	}

}
