package org.palladiosimulator.analyzer.slingshot.engine.ssj;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.EventDispatcher;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationEngine;
import org.palladiosimulator.analyzer.slingshot.simulation.core.entities.SimulationInformation;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.ConcreteTimeEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;

import com.google.common.base.Preconditions;

import umontreal.ssj.simevents.Event;
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
	private final SimulationInformationSSJ simulationInformationSSJ;

	@Inject
	public SimulationEngineSSJ(final EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
		this.simulator = new Simulator();
		this.simulationInformationSSJ = new SimulationInformationSSJ(this.simulator);
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
	public void scheduleEvent(final DESEvent event) {
		this.scheduleEvent(event, event.getDelay());
	}

	@Override
	public void scheduleEvent(final DESEvent event, final double delay) {
		LOGGER.info(EventPrettyLogPrinter.prettyPrint(event, "Received Event and added to FEL", "Simulation Engine"));
		if (event instanceof ConcreteTimeEvent) {
			final ConcreteTimeEvent timeEvent = (ConcreteTimeEvent) event;
			LOGGER.info("The event is going to be scheduled at " + timeEvent.at());
			this.scheduleEventAt(event, timeEvent.at());
		} else {
			final Event simulationEvent = new SSJEvent(event);
			simulationEvent.schedule(delay);
		}
	}

	@Override
	public void scheduleEventAt(final DESEvent event, final double pointInTime) {
		Preconditions.checkArgument(pointInTime >= this.getTime(),
				"You can only specify events to be scheduled"
						+ " at an exact point in time in the future, not in the past.");
		LOGGER.info("The following event will be dispatched exactly at " + pointInTime + " simulation time units: " + event.getClass().getSimpleName());
		final Event simulationEvent = new SSJEvent(event);
		simulationEvent.setTime(pointInTime + event.getDelay());
		this.simulator.getEventList().add(simulationEvent); // Since the exact time was given, we have to do it
															// manually.
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
	public void registerEventListener(final Object eventListener) {
		this.eventDispatcher.register(Preconditions.checkNotNull(eventListener, "Event-listeners must not be null!"));
	}
	
	@Override
	public SimulationInformation getSimulationInformation() {
		return this.simulationInformationSSJ;
	}

	private final class SSJEvent extends Event {

		private final DESEvent event;

		private SSJEvent(final DESEvent correspondingEvent) {
			super(SimulationEngineSSJ.this.simulator);
			this.event = correspondingEvent;
		}

		@Override
		public void actions() {
			LOGGER.info(EventPrettyLogPrinter.prettyPrint(this.event,
					"Executed evt routine from FEL and published to event bus", "SSJ Simulation Engine"));
			LOGGER.info("Current time is:" + SimulationEngineSSJ.this.simulator.time());

			if (this.event instanceof SimulationStarted) {
				SimulationEngineSSJ.this.eventDispatcher.unfreez();
			}

			this.event.setTime(SimulationEngineSSJ.this.simulator.time());

			SimulationEngineSSJ.this.eventDispatcher.post(this.event);
			SimulationEngineSSJ.this.simulationInformationSSJ.increaseNumberOfProcessedEvents();

			// simulationMonitoring.publishProbeEvent(event);

			if (this.event instanceof SimulationFinished) {
				/*
				 * In this case, no events should be published again afterwards.
				 */
				SimulationEngineSSJ.this.eventDispatcher.freez();
				SimulationEngineSSJ.this.simulator.stop();
			}
		}

	}
}
