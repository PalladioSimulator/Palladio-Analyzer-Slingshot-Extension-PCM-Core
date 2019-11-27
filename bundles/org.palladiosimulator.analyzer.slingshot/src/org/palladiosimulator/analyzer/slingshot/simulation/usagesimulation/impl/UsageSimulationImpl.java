package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventObserver;
import org.palladiosimulator.analyzer.slingshot.simulation.events.FinishUserEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.SimulationStart;
import org.palladiosimulator.analyzer.slingshot.simulation.events.StartUserEvent;
import org.palladiosimulator.pcm.usagemodel.UsageModel;


public class UsageSimulationImpl implements SimulationBehaviourExtension, EventObserver {
	
	private final Logger LOGGER = Logger.getLogger(UsageSimulationImpl.class);

	private UsageModelRepository usageModelRepository;
	private SimulatedUserProvider simulatedUserProvider;
	
	//FIXME:: moved simulatedUsers as part of the UsageSimulationImpl
	private List<SimulatedUser> simulatedUsers;
	
	
	private SimulationScheduling simulationScheduling;
	

	public UsageSimulationImpl(final UsageModelRepository usageModelRepository, final SimulatedUserProvider simulatedUserProvider) {
		this.usageModelRepository = usageModelRepository;
		this.simulatedUserProvider = simulatedUserProvider;
	}

	@Override
	public void init(final UsageModel usageModel, final SimulationScheduling simulationScheduling) {
		loadModel(usageModel);
		
		this.simulationScheduling = simulationScheduling;
		
		simulatedUsers = createSimulatedUsers();
		LOGGER.info(String.format("Created '%s' users for closed workload simulation", simulatedUsers.size()));
	}

	@Override
	public void update(DESEvent evt) {
		if(evt instanceof FinishUserEvent) {
			FinishUserEvent finishedUserEvent = FinishUserEvent.class.cast(evt);
			LOGGER.info(String.format("Previously scheduled userFinished '%s' has finished executing its event routine, Time To schedule a new StartUserEvent", finishedUserEvent.getId()));
			DESEvent nextEvt = findNextEvent(finishedUserEvent.getSimulatedUser());
			simulationScheduling.scheduleForSimulation(nextEvt);
		}
		
		if(evt instanceof SimulationStart) { 
			LOGGER.info("Oh, I received a SimulationStart event let schedule my initial event. ");

			List<DESEvent> initialEvents = new ArrayList<DESEvent>();
			for (SimulatedUser simulatedUser : simulatedUsers) {
				DESEvent startUserEvent = findStartEvent(simulatedUser);
				initialEvents.add(startUserEvent);
			}

			LOGGER.info(String.format("Simulation Driver Please schedule the following list of events: '%s'",initialEvents));
			simulationScheduling.scheduleForSimulation(initialEvents);
		}
		
	}
	
	@Override
	public EventObserver getSimulationEventObserver() {
		// TODO Auto-generated method stub
		return this;
	}
	
	//Private helper methods to achieve the internal behavior
	
	private void loadModel(final UsageModel usageModel) {
		usageModelRepository.load(usageModel);
		simulatedUserProvider.initializeRepository(usageModelRepository);
		LOGGER.info("UsageSimulation: usage model was loaded.");
	}

	private List<SimulatedUser> createSimulatedUsers() {
		return simulatedUserProvider.createSimulatedUsers();
	}

	private DESEvent findStartEvent(SimulatedUser user) {
		return new StartUserEvent(user);
	}

	private DESEvent findNextEvent(SimulatedUser user) {
		// TODO Auto-generated method stub
		
		// return user.nextEvent();

		return new StartUserEvent(user);
	}
}
