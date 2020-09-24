package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.model;

import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationModel;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

/**
 * A concrete instance of a simulation model.
 * 
 * @author Julijan Katic
 */
public class SimulizarSimulationModel implements SimulationModel {

	private final UsageModel usageModel;
	private final Allocation allocation;

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
		return allocation;
	}

}
