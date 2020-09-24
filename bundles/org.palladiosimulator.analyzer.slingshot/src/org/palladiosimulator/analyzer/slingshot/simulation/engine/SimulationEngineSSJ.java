package org.palladiosimulator.analyzer.slingshot.simulation.engine;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;

import com.google.common.eventbus.EventBus;

import umontreal.ssj.simevents.Event;
import umontreal.ssj.simevents.Sim;
import umontreal.ssj.simevents.Simulator;

/**
 * This class defines the simulation engine by utilizing the SSJ library.
 * 
 * @author Julijan Katic
 */
public class SimulationEngineSSJ implements SimulationEngine {

	private final Logger LOGGER = Logger.getLogger(SimulationEngineSSJ.class);

	private final EventBus eventBus;

	private final Simulator simulator;

	@Override
	public EventBus getEventDispatcher() {
		return eventBus;
	}

	public SimulationEngineSSJ() {
		this.eventBus = new EventBus();
		this.simulator = new Simulator();
	}

	@Override
	public void scheduleEvent(final DESEvent event) {
		// this code should go in the right place
		scheduleEvent(event, event.getDelay());

	}

	@Override
	public double getTime() {
		return simulator.time();
	}

	@Override
	public void start() {
		LOGGER.info("********** SimulationEngineSSJ.start **********");
		simulator.start();
	}

	@Override
	public boolean hasScheduledEvents() {
		return Sim.getEventList().listIterator().hasNext();
	}

	@Override
	public void init() {
		simulator.init();
	}

	@Override
	public void scheduleEvent(final DESEvent event, final double delay) {
		LOGGER.info(EventPrettyLogPrinter.prettyPrint(event, "Received Event and added to FEL", "Simulation Engine"));

		final Event myev = new Event(simulator) {
			@Override
			public void actions() {
				LOGGER.info(EventPrettyLogPrinter.prettyPrint(event,
						"Executed evt routine from FEL and published to event bus", "SSJ Simulation Engine"));
				LOGGER.info("Current time is:" + simulator.time());

				// set time of the execution
				event.setTime(simulator.time());

				// publish the event to the bus
				eventBus.post(event);
			}
		};
		myev.schedule(delay);

	}

}
