package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.repositories.UsageModelRepository;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserWokeUp;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserSlept;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import com.google.common.eventbus.Subscribe;

@OnEvent(eventType = SimulationStarted.class, outputEventType = UserStarted.class, cardinality = EventCardinality.MANY)
//@OnEvent(eventType = UserStarted.class, outputEventType = UserFinished.class, cardinality = EventCardinality.SINGLE)
@OnEvent(eventType = UserFinished.class, outputEventType = DESEvent.class, cardinality = EventCardinality.SINGLE)
@OnEvent(eventType = UserWokeUp.class, outputEventType = DESEvent.class, cardinality = EventCardinality.SINGLE)
public class UsageSimulationImpl implements SimulationBehaviourExtension {
	
	private final Logger LOGGER = Logger.getLogger(UsageSimulationImpl.class);

	// internal property for the behavior
	private List<SimulatedUser> simulatedUsers;

	// dependency on the core
	private UsageModelRepository usageModelRepository;
	private SimulatedUserProvider simulatedUserProvider;
	
	public UsageSimulationImpl() {
		
	}

	public UsageSimulationImpl(final UsageModelRepository usageModelRepository, final SimulatedUserProvider simulatedUserProvider) {
		this.usageModelRepository = usageModelRepository;
		this.simulatedUserProvider = simulatedUserProvider;
	}

	@Override
	public void init(final UsageModel usageModel) {
		loadModel(usageModel);
		simulatedUsers = createSimulatedUsers();
		LOGGER.info(String.format("Created '%s' users for closed workload simulation", simulatedUsers.size()));
	}

	// @OnEvent(output=UserStarted -> checker its fine ... -> check them all) 
	// @OnEvnet(output=DESEvent -> reject immediatly -> fine
	// FIXME: can these checks be done all at compile-time, so why we need runtime checks
	// FIXME: how can it occur that an extension sends an event which is not specified
	// FIXME: when and how could it happen that an extension sends an event which has no specification for it.
	// FIXME: explore runtime checks and in combination with rule checks.
	// TODO: Also when enforcing the contract rather then observing the result object directly we could easily check whether 
	// the method that was invoked has the generic type of UserStarted.
	@Subscribe public ResultEvent<UserStarted> onSimulationStart(SimulationStarted evt) {
		Set<UserStarted> initialEvents = new HashSet<UserStarted>();
		for (SimulatedUser simulatedUser : simulatedUsers) {
			UserStarted startUserEvent = findStartEvent(simulatedUser);
			initialEvents.add(startUserEvent);
		}		
		ResultEvent<UserStarted> initialUserStartedEvents = new ResultEvent<UserStarted>(initialEvents);
		return initialUserStartedEvents;
	}
	
	
//  Example method violating the contract for types
//	@Subscribe public ResultEvent<UserFinished> onSimulationStart(SimulationStarted evt) {
//		Set<UserFinished> initialEvents = new HashSet<UserFinished>();
//		initialEvents.add(new UserFinished(null));
//		ResultEvent<UserFinished> initialUserStartedEvents = new ResultEvent<UserFinished>(initialEvents);
//		return initialUserStartedEvents;
//	}

	@Subscribe public ResultEvent<DESEvent> onFinishUserEvent(UserFinished evt) {
		LOGGER.info(String.format("Previously scheduled userFinished '%s' has finished executing its event routine, Time To schedule a new StartUserEvent", evt.getId()));
		DESEvent nextEvt = createNextEvent(evt.getSimulatedUser());
		return new ResultEvent<DESEvent>(Set.of(nextEvt));
	}
	
//	@Subscribe public ResultEvent<UserFinished> onUserStarted(UserStarted evt) {
//		return new ResultEvent<UserFinished>(Set.of(new UserFinished(evt.getSimulatedUser())));
//	}
	
	@Subscribe public ResultEvent<DESEvent> onWakeUpUserEvent(UserWokeUp evt) {
		DESEvent nextEvt = createNextEvent(evt.getSimulatedUser());
		return new ResultEvent<DESEvent>(Set.of(nextEvt));
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
	private UserStarted findStartEvent(SimulatedUser user) {
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
