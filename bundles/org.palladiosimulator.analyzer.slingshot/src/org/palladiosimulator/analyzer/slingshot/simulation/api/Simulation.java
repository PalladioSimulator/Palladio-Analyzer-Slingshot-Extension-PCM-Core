package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.pcm.usagemodel.UsageModel;

public interface Simulation {

	void init(UsageModel usageModel) throws Exception;
	
	void startSimulation();
	
}
