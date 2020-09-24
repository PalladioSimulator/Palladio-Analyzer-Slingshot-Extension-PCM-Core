package org.palladiosimulator.analyzer.slingshot.simulation.api;

import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral.BehaviorExtensionsHandler;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral.decorators.DecoratedSimulationBehaviorProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.model.SimulizarSimulationModel;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineSSJ;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SimulationFactory {

	public static Simulation createSimulation() throws Exception {

		final SimulationEngine simEngine = new SimulationEngineSSJ();

		final List<DecoratedSimulationBehaviorProvider> providers = BehaviorExtensionsHandler.getAllProviders();

		// Simulation Driver
		final SimulationDriver simulationDriver = new SimulationDriver(simEngine, providers);

		return simulationDriver;
	}

	public static SimulationModel createSimulizarSimulationModel(final UsageModel usageModel,
			final Allocation allocation) {
		return new SimulizarSimulationModel(usageModel, allocation);
	}

}
