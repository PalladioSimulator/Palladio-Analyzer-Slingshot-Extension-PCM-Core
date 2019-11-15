package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.analyzer.slingshot.repositories.impl.UsageModelRepositoryImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineMock;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUserProvider;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UsageSimulation;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UsageSimulationImpl;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SimulationFactory {
	
	public static Simulation createSimulation(final UsageModel usageModel) {
		UsageModelRepositoryImpl usageModelRepository = new UsageModelRepositoryImpl(usageModel);
		SimulatedUserProvider simulatedUserProvider = new SimulatedUserProvider();
		SimulationEngine simEngine = new SimulationEngineMock();
		UsageSimulation usageSimulation = new UsageSimulationImpl(usageModelRepository, simulatedUserProvider);
		return new SimulationDriver(usageSimulation, simEngine);
	}

}
