package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventObserver;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserWokeUp;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserSlept;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import com.google.common.eventbus.Subscribe;


public class UsageSimulationImpl implements SimulationBehaviourExtension {
	
	private final Logger LOGGER = Logger.getLogger(UsageSimulationImpl.class);

	// internal property for the behavior
	private List<SimulatedUser> simulatedUsers;

	// dependency on the core
	private UsageModelRepository usageModelRepository;
	private SimulatedUserProvider simulatedUserProvider;
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


	@Subscribe public List<DESEvent> onSimulationStart(SimulationStarted evt) {
		LOGGER.info("Oh, I received a SimulationStart event let schedule my initial event. ");
		List<DESEvent> initialEvents = new ArrayList<DESEvent>();
		for (SimulatedUser simulatedUser : simulatedUsers) {
			DESEvent startUserEvent = findStartEvent(simulatedUser);
			initialEvents.add(startUserEvent);
		}
		LOGGER.info(String.format("Simulation Driver Please schedule the following list of events: '%s'",initialEvents));
		simulationScheduling.scheduleForSimulation(initialEvents);
		return initialEvents;
	}
	
	@Subscribe public DESEvent onFinishUserEvent(UserFinished evt) {
		LOGGER.info(String.format("Previously scheduled userFinished '%s' has finished executing its event routine, Time To schedule a new StartUserEvent", evt.getId()));
		DESEvent nextEvt = createNextEvent(evt.getSimulatedUser());
		simulationScheduling.scheduleForSimulation(nextEvt);
		return nextEvt;
	}
	
	@Subscribe public DESEvent onWakeUpUserEvent(UserWokeUp evt) {
		DESEvent nextEvt = createNextEvent(evt.getSimulatedUser());
		simulationScheduling.scheduleForSimulation(nextEvt);
		return evt;
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

	// TODO:: Fix interpretation
	private DESEvent findStartEvent(SimulatedUser user) {
		return new UserStarted(user);
	}

	private DESEvent createNextEvent(SimulatedUser user) {
		AbstractUserAction nextAction = user.nextAction();		
		if (null == nextAction) {
			LOGGER.info(String.format("SimulatedUser['%s'|'%s']: no more actions found.", user.getUserName(), user.getUserId()));
			// Time to sleep for the think time
			// Schedule UserSleep -> inside the event routine -> schedule WakeUpUserEvent 
			
			
		} else {
			if (nextAction instanceof Delay) {
				LOGGER.info(String.format("SimulatedUser['%s'|'%s']: scheduled DelayEvent", user.getUserName(), user.getUserId()));
				// here is the point where we extract the information and we pass it to the UserSleep event. 
				// this involves callling StoEx libraries to determine the time needed for this user to sleep
				//FIXME::currently hardcoded
				double timeToSleep = 10;
				return new UserSlept(user, timeToSleep);
				
			} else if (nextAction instanceof Stop) {
				LOGGER.info(String.format("SimulatedUser['%s'|'%s']: scheduled StopEvent", user.getUserName(), user.getUserId()));
				double thinkTime = 10;
				return new UserSlept(user, thinkTime);
			}
		}
		return new UserStarted(user);
	}
	
}
