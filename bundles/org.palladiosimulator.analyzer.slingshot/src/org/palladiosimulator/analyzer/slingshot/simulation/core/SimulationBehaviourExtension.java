package org.palladiosimulator.analyzer.slingshot.simulation.core;

import org.palladiosimulator.analyzer.slingshot.simulation.events.EventObserver;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public interface SimulationBehaviourExtension {
	
	// later here we pass an abstract data interface
	void init(UsageModel usageModel, SimulationScheduling simulationScheduling);
	
}
