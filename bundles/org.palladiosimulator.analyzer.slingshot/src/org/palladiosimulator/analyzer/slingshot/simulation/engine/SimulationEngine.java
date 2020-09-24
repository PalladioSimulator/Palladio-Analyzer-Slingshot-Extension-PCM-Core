
package org.palladiosimulator.analyzer.slingshot.simulation.engine;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

import com.google.common.eventbus.EventBus;

/**
 * The engine handles the simulation and schedules the next events.
 * 
 * @author Julijan Katic
 */
public interface SimulationEngine {

	/**
	 * Initializes the simulation engine.
	 */
	void init();

	/**
	 * Starts the simulation.
	 */
	void start();

	/**
	 * Schedules the next event and publishes it to the {@link EventBus}.
	 * 
	 * @param event the non-null event.
	 */
	void scheduleEvent(DESEvent event);

	/**
	 * Publishes an event to the EventBus with a certain delay.
	 * 
	 * @param event The non-null event.
	 * @param delay The non-negative delay.
	 */
	void scheduleEvent(DESEvent event, double delay);

	/**
	 * Returns the simulated time.
	 * 
	 * @return The current simulation time.
	 */
	double getTime();

	/**
	 * Checks whether it has any scheduled events left.
	 * 
	 * @return true iff it has scheduled events.
	 */
	boolean hasScheduledEvents();

	/**
	 * Returns the event bus.
	 */
	EventBus getEventDispatcher();
}
