package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

/*
 * TODO: Use DI instead.
 */
/**
 * The simulation model that is needed for simulation. This currently provides a
 * UsageModel and a Allocation model.
 * 
 * @author Julijan Katic
 */
public interface SimulationModel {

	UsageModel getUsageModel();

	Allocation getAllocation();

}
