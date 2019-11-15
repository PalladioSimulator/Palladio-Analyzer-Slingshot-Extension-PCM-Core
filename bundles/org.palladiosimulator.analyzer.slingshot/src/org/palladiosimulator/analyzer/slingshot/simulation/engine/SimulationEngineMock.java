package org.palladiosimulator.analyzer.slingshot.simulation.engine;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public class SimulationEngineMock implements SimulationEngine {
	
	private final Logger LOGGER = Logger.getLogger(SimulationEngineMock.class);
	
	private List<DESEvent> futureEventList;
	
	public SimulationEngineMock() {
		this.futureEventList = new ArrayList<DESEvent>();
	}

	@Override
	public void scheduleEvent(DESEvent event) {
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
		
		
		while(!futureEventList.isEmpty()) {
			DESEvent nextEvent = futureEventList.remove(0);
			LOGGER.info(String.format("*** Handle event ['%s']", nextEvent.getId()));
			nextEvent.handle();
		}
		
		LOGGER.info("********** SimulationEngineMock.start ---  finished due to empty FEL *********");
	}

	@Override
	public boolean hasScheduledEvents() {
		if (!futureEventList.isEmpty()) {
			return true;
		}
		return false;
	}


}
