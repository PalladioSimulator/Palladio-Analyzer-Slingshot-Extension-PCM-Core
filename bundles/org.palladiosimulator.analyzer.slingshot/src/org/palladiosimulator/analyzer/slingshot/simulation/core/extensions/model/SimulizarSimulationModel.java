package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.model;

import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationModel;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class SimulizarSimulationModel implements SimulationModel {
	
	private UsageModel usageModel;
	private Allocation allocation;
	
	public SimulizarSimulationModel(final UsageModel usageModel, final Allocation allocation) {
		this.usageModel = usageModel;
		this.allocation = allocation;
	}
	

	@Override
	public UsageModel getUsageModel() {
		return usageModel;
	}


	@Override
	public Allocation getAllocation() {
		// TODO Auto-generated method stub
		return allocation;
	}
	
	

}
