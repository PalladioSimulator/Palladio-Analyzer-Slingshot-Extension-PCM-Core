package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.engine.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;

import com.google.common.eventbus.EventBus;

/**
 * Mocks the simulation engine for test purposes.
 */
public class SimulationEngineMock implements SimulationEngine {

	private final Logger LOGGER = Logger.getLogger(SimulationEngineMock.class);

	/**
	 * Maximum amount of events to simulate. When this threshold is reached, the
	 * simulation stops.
	 */
	private final int STOPPING_CONDITION = 100;

	private final List<DESEvent> futureEventList;

	private final EventBus eventBus;

	public SimulationEngineMock() {
		this.futureEventList = new LinkedList<>();
		this.eventBus = new EventBus();
	}

	@Override
	public EventBus getEventDispatcher() {
		return eventBus;
	}

	@Override
	public void scheduleEvent(final DESEvent event) {
		futureEventList.add(event);
		LOGGER.info(EventPrettyLogPrinter.prettyPrint(event, "Received Event and added to FEL", "Simulation Engine"));
	}

	@Override
	public void init() {
		LOGGER.info("********** SimulationEngineMock.init --- *********");
	}

	@Override
	public void start() {
		LOGGER.info("********** SimulationEngineMock.start **********");

		int simulatedEvents = 0;

		while (!futureEventList.isEmpty() && simulatedEvents < STOPPING_CONDITION) {
			simulatedEvents++;

			final DESEvent nextEvent = futureEventList.remove(0);

			eventBus.post(nextEvent);
		}

		LOGGER.info(
				"********** SimulationEngineMock.start ---  finished due to empty FEL or Stopping Condition*********");
	}

	@Override
	public void scheduleEvent(final DESEvent event, final double delay) {
		scheduleEvent(event);
	}

	/**
	 * @return always 0
	 */
	@Override
	public double getTime() {
		return 0;
	}

	@Override
	public boolean hasScheduledEvents() {
		return !futureEventList.isEmpty();
	}

}
