package org.palladiosimulator.analyzer.slingshot.simulation.core;

import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationModel;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SimulizarSimulationModel implements SimulationModel {
	
	private UsageModel usageModel;
	
	public SimulizarSimulationModel(final UsageModel usageModel) {
		this.usageModel = usageModel;
	}
	

	@Override
	public UsageModel getUsageModel() {
		return usageModel;
	}

}
