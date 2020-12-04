package org.palladiosimulator.analyzer.slingshot.simulation.engine;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;

import com.google.common.base.Preconditions;
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

	private static final Logger LOGGER = Logger.getLogger(SimulationEngineSSJ.class);

	private final EventBus eventBus;

	private final Simulator simulator;

	/**
	 * An exception handler for subscribed event handlers. This is needed as
	 * exceptions thrown by event handlers will remain in the event bus context.
	 */
	private SimulationEventExceptionHandler exceptionHandler;

	/**
	 * Instantiates this class without an exception handler for the eventbus.
	 */
	public SimulationEngineSSJ() {
		this(null);
	}

	/**
	 * Instantiates a class with a given exception handler for the eventbus. This
	 * exception handler can be {@code null}. If the exception handler is
	 * {@code null}, the default behavior will be logging the exception in the
	 * console. Nevertheless, the exception will not reach the main call stack, but
	 * will remain in the event bus context, and must be handled there.
	 * 
	 * @param exceptionHandler The exception handler for the event bus.
	 */
	public SimulationEngineSSJ(final SimulationEventExceptionHandler exceptionHandler) {
		this.loadEventExceptionHandler(exceptionHandler);

		/* Creates the event bus with the exception handler. */
		this.eventBus = new EventBus((exception, context) -> {
			if (this.exceptionHandler != null) {
				final DESEvent returnedEvent = this.exceptionHandler.onException(exception,
				        (DESEvent) context.getEvent());
				if (returnedEvent != null) {
					context.getEventBus().post(returnedEvent);
				}
			} else {
				LOGGER.error(exception);
			}
		});
		this.simulator = new Simulator();
	}

	@Override
	public void scheduleEvent(final DESEvent event) {
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

	@Override
	public void loadEventExceptionHandler(final SimulationEventExceptionHandler handler) {
		this.exceptionHandler = handler;
	}

	@Override
	public void registerEventListener(final Object eventListener) {
		Preconditions.checkNotNull(eventListener, "Event-listeners must not be null!");
		this.eventBus.register(eventListener);
	}

}
