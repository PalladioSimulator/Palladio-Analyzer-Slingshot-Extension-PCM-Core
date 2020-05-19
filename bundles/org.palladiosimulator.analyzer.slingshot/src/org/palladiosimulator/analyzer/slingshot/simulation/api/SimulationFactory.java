package org.palladiosimulator.analyzer.slingshot.simulation.api;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.repositories.impl.UsageModelRepositoryImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulizarSimulationModel;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.decorators.DecoratedResourceBehaviorProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.decorators.DecoratedSystemBehaviorProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.decorators.DecoratedUsageSimulationProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineSSJ;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUserProvider;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SimulationFactory {
		
	public static Simulation createSimulation() throws Exception {
		// The first data needed for the extensions
		UsageModelRepositoryImpl usageModelRepository = new UsageModelRepositoryImpl();
		SimulatedUserProvider simulatedUserProvider = new SimulatedUserProvider();
		
		// The Core
//		SimulationEngine simEngine = new SimulationEngineMock();
		SimulationEngine simEngine = new SimulationEngineSSJ();
		
		// Extensions
		DecoratedUsageSimulationProvider decoratedUsageSimulationProvider = new DecoratedUsageSimulationProvider(usageModelRepository, simulatedUserProvider);
		DecoratedResourceBehaviorProvider decoratedResourceBehavoirProvider = new DecoratedResourceBehaviorProvider();
		DecoratedSystemBehaviorProvider decoratedSystemBehaviorProvider = new DecoratedSystemBehaviorProvider();
		
		// Simulation Driver
		SimulationDriver simulationDriver =  new SimulationDriver(simEngine, List.of(decoratedUsageSimulationProvider, decoratedResourceBehavoirProvider, decoratedSystemBehaviorProvider));
						
		return simulationDriver;
	}
	
	
	public static SimulationModel createSimulizarSimulationModel(UsageModel usageModel, Allocation allocation) {
		return new SimulizarSimulationModel(usageModel, allocation);
	}

}
