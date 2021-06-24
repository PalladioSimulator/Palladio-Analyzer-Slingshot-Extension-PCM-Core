package org.palladiosimulator.analyzer.slingshot.simulation.api;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * This interface is used for simulation engines to handle exceptions.
 * Typically, event bus system's do not allow exceptions to go up the call stack
 * and leave the event bus system, but remain only in the event bus system.
 * 
 * @author Julijan Katic
 */
@FunctionalInterface
public interface SimulationEventExceptionHandler {

	/**
	 * Handles the exception that is thrown while processing the event, and returns
	 * another event to be scheduled if needed. The return value can be
	 * {@code null}, and it is up to the exception handler
	 * 
	 * @param exception
	 * @param event
	 */
	void onException(final Throwable exception, final DESEvent event);

}
