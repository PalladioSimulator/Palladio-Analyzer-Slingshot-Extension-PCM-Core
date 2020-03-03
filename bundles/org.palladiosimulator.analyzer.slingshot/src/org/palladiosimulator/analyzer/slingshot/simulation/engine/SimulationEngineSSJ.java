package org.palladiosimulator.analyzer.slingshot.simulation.engine;

import com.google.common.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;

import umontreal.ssj.*;
import umontreal.ssj.simevents.Event;
import umontreal.ssj.simevents.Sim;

public class SimulationEngineSSJ implements SimulationEngine {
	
	private final Logger LOGGER = Logger.getLogger(SimulationEngineSSJ.class);
	
	private final int STOPPING_CONDITION = 100;
	
	private final EventBus eventBus;
	
	@Override
	public EventBus getEventDispatcher() {
		return eventBus;
	}


	public SimulationEngineSSJ() {
		this.eventBus = new EventBus();
	}

	@Override
	public void scheduleEvent(DESEvent event) {
		//this code should go in the right place
		scheduleEvent(event, 0);
		
	}

	@Override
	public void getTime() {
		
	}

	@Override
	public void start() {
		LOGGER.info("********** SimulationEngineSSJ.start **********");		
		Sim.start();
	}

	@Override
	public boolean hasScheduledEvents() {
		return Sim.getEventList().listIterator().hasNext();
	}


	@Override
	public void init() {
		Sim.init();
	}


	@Override
	public void scheduleEvent(DESEvent event, double delay) {
		LOGGER.info(EventPrettyLogPrinter.prettyPrint(event, "Received Event and added to FEL", "Simulation Engine"));
		
		new Event() {
			@Override
			public void actions() {
				event.handle();
				LOGGER.info(EventPrettyLogPrinter.prettyPrint(event, "Executed evt routine from FEL and published to event bus", "SSJ Simulation Engine"));
				eventBus.post(event);
			}
		}.schedule(0);
	}

}
