package org.palladiosimulator.analyzer.slingshot.simulation.engine;

import com.google.common.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public class SimulationEngineMock implements SimulationEngine {
	
	private final Logger LOGGER = Logger.getLogger(SimulationEngineMock.class);
	
	private final int STOPPING_CONDITION = 100;
	
	private List<DESEvent> futureEventList;
	
	private final EventBus eventBus;
	
	@Override
	public EventBus getEventDispatcher() {
		return eventBus;
	}


	public SimulationEngineMock() {
		this.futureEventList = new ArrayList<DESEvent>();
		this.eventBus = new EventBus();
	}

	@Override
	public void scheduleEvent(DESEvent event) {
		//this code should go in the right place
		futureEventList.add(event);
		LOGGER.info(String.format("*** Scheduled new event '%s' *** ", event.getId()));
	}

	@Override
	public void getTime() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void start() {
		LOGGER.info("********** SimulationEngineMock.start    **********");
		
		int simulatedEvents = 0;
		
		while(!futureEventList.isEmpty() && simulatedEvents<STOPPING_CONDITION) {
			
			simulatedEvents++;
			
			//the semantic here is incorrect because it needs to remove the next event that will happen first.
			//and it may be the case that for example I user is scheduled in the next second so its not FCFS on the event list
			DESEvent nextEvent = futureEventList.remove(0);
			LOGGER.info(String.format("*** Handle event ['%s']", nextEvent.getId()));
			nextEvent.handle();
			//TODO:: Schedule the side-effects of that event.
			// nextEvent is an object with operations 
			// we want to post only the data so that components dont see the eventRoutine 
			// because they dont need it and cant do anything with it. it is idempotent and cant effect the fel

			eventBus.post(nextEvent);
		}
		
		
		LOGGER.info("********** SimulationEngineMock.start ---  finished due to empty FEL or Stopping Condition*********");
	}

	@Override
	public boolean hasScheduledEvents() {
		if (!futureEventList.isEmpty()) {
			return true;
		}
		return false;
	}

}
