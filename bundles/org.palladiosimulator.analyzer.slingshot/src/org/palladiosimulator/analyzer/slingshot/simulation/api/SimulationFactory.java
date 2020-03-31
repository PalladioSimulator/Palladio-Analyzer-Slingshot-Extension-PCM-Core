package org.palladiosimulator.analyzer.slingshot.simulation.api;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.repositories.impl.UsageModelRepositoryImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.decorators.DecoratedResourceBehaviorProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.decorators.DecoratedUsageSimulationProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineMock;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineSSJ;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.ResourceSimulationImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUserProvider;

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
					
		// Simulation Driver
		SimulationDriver simulationDriver =  new SimulationDriver(simEngine, List.of(decoratedUsageSimulationProvider, decoratedResourceBehavoirProvider));
						
		return simulationDriver;
	}

}
