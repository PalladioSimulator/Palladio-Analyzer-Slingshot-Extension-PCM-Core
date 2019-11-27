package org.palladiosimulator.analyzer.slingshot.simulation.api;

import java.util.ArrayList;

import org.palladiosimulator.analyzer.slingshot.repositories.impl.UsageModelRepositoryImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineMock;
import org.palladiosimulator.analyzer.slingshot.simulation.events.Dispatcher;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUserProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UsageSimulationImpl;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SimulationFactory {
	
	public static Simulation createSimulation() {
		// The first SimulationBehaviourExtension
		UsageModelRepositoryImpl usageModelRepository = new UsageModelRepositoryImpl();
		SimulatedUserProvider simulatedUserProvider = new SimulatedUserProvider();
		SimulationBehaviourExtension usageSimulation = new UsageSimulationImpl(usageModelRepository, simulatedUserProvider);	
		
		// The Core
		Dispatcher eventDispatcher = new Dispatcher();
		SimulationEngine simEngine = new SimulationEngineMock(eventDispatcher);
		
		// Add additional constructor which has a list of extension.
		var simulationBehaviorExtensions = new ArrayList<SimulationBehaviourExtension>();
		simulationBehaviorExtensions.add(usageSimulation);
		SimulationDriver simulationDriver =  new SimulationDriver(simEngine,simulationBehaviorExtensions);
	
				
		return simulationDriver;
	}

}
