package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.pcm.usagemodel.UsageModel;
import org.palladiosimulator.pcm.allocation.Allocation;


public interface SimulationModel {
	
	UsageModel getUsageModel();
	Allocation getAllocation();

}
