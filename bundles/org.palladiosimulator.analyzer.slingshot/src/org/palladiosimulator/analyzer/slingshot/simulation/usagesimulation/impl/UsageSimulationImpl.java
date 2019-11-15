package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.StartUserEvent;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

public class UsageSimulationImpl implements UsageSimulation {
	
	private final Logger LOGGER = Logger.getLogger(UsageSimulationImpl.class);

	private UsageModelRepository usageModelRepository;
	private SimulatedUserProvider simulatedUserProvider;
	
	public UsageSimulationImpl(final UsageModelRepository usageModelRepository, final SimulatedUserProvider simulatedUserProvider) {
		this.usageModelRepository = usageModelRepository;
		this.simulatedUserProvider = simulatedUserProvider;
	}

	@Override
	public void loadModel(final UsageModel usageModel) {
		usageModelRepository.load(usageModel);
		simulatedUserProvider.initializeRepository(usageModelRepository);
		LOGGER.info("UsageSimulation: usage model was loaded.");
	}

	@Override
	public List<SimulatedUser> createSimulatedUsers() {
		return simulatedUserProvider.createSimulatedUsers();
	}


	@Override
	public DESEvent findStartEvent(SimulatedUser user) {
		return new StartUserEvent(user);
	}

	
	@Override
	public DESEvent findNextEvent(SimulatedUser user) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
