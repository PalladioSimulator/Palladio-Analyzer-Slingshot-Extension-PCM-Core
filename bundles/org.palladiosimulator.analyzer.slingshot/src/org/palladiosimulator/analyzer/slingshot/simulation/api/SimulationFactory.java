package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationDriver;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngineMock;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SimulationFactory {
	
	public static Simulation createSimulation(final UsageModel usageModel) {
		SimulationEngine simEngine = new SimulationEngineMock();
		return new SimulationDriver(simEngine, usageModel);
		
	}

}
