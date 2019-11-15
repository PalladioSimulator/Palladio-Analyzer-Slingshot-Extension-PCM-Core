package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.analyzer.slingshot.repositories.impl.UsageModelRepositoryImpl;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineMock;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUserProvider;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SimulationFactory {
	
	public static Simulation createSimulation(final UsageModel usageModel) {
		UsageModelRepositoryImpl usageModelRepository = new UsageModelRepositoryImpl(usageModel);
		SimulatedUserProvider simulatedUserProvider = new SimulatedUserProvider(usageModelRepository);
		SimulationEngine simEngine = new SimulationEngineMock();
		return new SimulationDriver(simulatedUserProvider , simEngine);
	}

}
