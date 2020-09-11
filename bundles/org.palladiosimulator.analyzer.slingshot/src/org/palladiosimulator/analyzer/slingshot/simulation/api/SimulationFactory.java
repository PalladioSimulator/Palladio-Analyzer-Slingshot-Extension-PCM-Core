package org.palladiosimulator.analyzer.slingshot.simulation.api;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.repositories.impl.UsageModelRepositoryImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.BehaviourExtensionsHandler;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.decorators.DecoratedSimulationBehaviorProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.model.SimulizarSimulationModel;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineSSJ;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUserProvider;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SimulationFactory {
	
	public static Simulation createSimulation() throws Exception {
		// The first data needed for the extensions
		final UsageModelRepositoryImpl usageModelRepository = new UsageModelRepositoryImpl();
		final SimulatedUserProvider simulatedUserProvider = new SimulatedUserProvider();
		
		// The Core
//		SimulationEngine simEngine = new SimulationEngineMock();
		final SimulationEngine simEngine = new SimulationEngineSSJ();
		
		// Extensions
		//final DecoratedUsageSimulationProvider decoratedUsageSimulationProvider = new DecoratedUsageSimulationProvider(usageModelRepository, simulatedUserProvider);
		//final DecoratedResourceBehaviorProvider decoratedResourceBehavoirProvider = new DecoratedResourceBehaviorProvider();
		//final DecoratedSystemBehaviorProvider decoratedSystemBehaviorProvider = new DecoratedSystemBehaviorProvider();
		
		final List<DecoratedSimulationBehaviorProvider> providers = BehaviourExtensionsHandler.getAllProviders();
		
		// Simulation Driver
		final SimulationDriver simulationDriver =  new SimulationDriver(simEngine, providers);
						
		return simulationDriver;
	}
	
	
	public static SimulationModel createSimulizarSimulationModel(final UsageModel usageModel, final Allocation allocation) {
		return new SimulizarSimulationModel(usageModel, allocation);
	}

}
