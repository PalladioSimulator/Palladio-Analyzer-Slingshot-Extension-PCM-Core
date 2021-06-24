package org.palladiosimulator.analyzer.slingshot.engine.ssj;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.EventDispatcher;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationMonitoring;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;

import com.google.common.base.Preconditions;

import umontreal.ssj.simevents.Event;
import umontreal.ssj.simevents.Sim;
import umontreal.ssj.simevents.Simulator;

/**
 * This class defines the simulation engine by utilizing the SSJ library.
 * 
 * @author Julijan Katic
 */
@Singleton
public final class SimulationEngineSSJ implements SimulationEngine {

	private static final Logger LOGGER = Logger.getLogger(SimulationEngineSSJ.class);
	
	private final EventDispatcher eventDispatcher;
	
	private final Simulator simulator;
	
	private final SimulationMonitoring simulationMonitoring;
	
	@Inject
	public SimulationEngineSSJ(final EventDispatcher eventDispatcher, final SimulationMonitoring simulationMonitoring) {
		this.eventDispatcher = eventDispatcher;
		this.simulationMonitoring = simulationMonitoring;
		this.simulator = new Simulator();
	}
	
	@Override
	public void init() {
		this.simulator.init();
	}

	@Override
	public void start() {
		LOGGER.info("********** SimulationEngineSSJ.start **********");
		this.simulator.start();
	}

	@Override
	public void scheduleEvent(DESEvent event) {
		this.scheduleEvent(event, event.getDelay());
	}

	@Override
	public void scheduleEvent(DESEvent event, double delay) {
		LOGGER.info(EventPrettyLogPrinter.prettyPrint(event, "Received Event and added to FEL", "Simulation Engine"));
		
		final Event simulationEvent = new Event(this.simulator) {
			
			@Override
			public void actions() {
				LOGGER.info(EventPrettyLogPrinter.prettyPrint(event,
						"Executed evt routine from FEL and published to event bus", "SSJ Simulation Engine"));
				LOGGER.info("Current time is:" + simulator.time());
				
				if (event instanceof SimulationStarted) {
					eventDispatcher.unfreez();
				}
				
				event.setTime(simulator.time());
				
				eventDispatcher.post(event);
				
				simulationMonitoring.publishProbeEvent(event);
				
				if (event instanceof SimulationFinished) {
					/*
					 * In this case, no events should be published again afterwards.
					 */
					eventDispatcher.freez();
				}
				
			}
		};
		
		simulationEvent.schedule(delay);
	}

	@Override
	public double getTime() {
		return this.simulator.time();
	}

	@Override
	public boolean hasScheduledEvents() {
		return !this.simulator.getEventList().isEmpty();
	}

	@Override
	public void registerEventListener(Object eventListener) {
		this.eventDispatcher.register(Preconditions.checkNotNull(eventListener, "Event-listeners must not be null!"));
	}

}
