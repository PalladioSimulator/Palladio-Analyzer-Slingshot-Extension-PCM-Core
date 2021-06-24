package org.palladiosimulator.analyzer.slingshot.simulation.api;

import com.google.common.eventbus.Subscribe;

/**
 * An event dispatcher is able to register classes listening to events, as well
 * as posting events. This interface is especially designed to define the
 * requirements Slingshot needs for such a dispatcher.
 * 
 * @author Julijan Katic
 *
 */
public interface EventDispatcher {

	/**
	 * Registers an instance for listening to events. The events are event-methods
	 * annotated by {@link Subscribe}.
	 * 
	 * @param object The instance which event-handler methods to register.
	 */
	public void register(final Object object);

	/**
	 * Unregisters an instance again if it was registered before. If not, then
	 * nothing should happen.
	 * 
	 * @param object The instance to unregister.
	 */
	public void unregister(final Object object);

	/**
	 * Posts an event and calls every registered event method whose single parameter
	 * has the type of the event.
	 * 
	 * @param event The event to post.
	 */
	public void post(final Object event);

	/**
	 * Freezes the event handler. Even events that are still in the queue won't be
	 * published. After calling this method, it is not possible to post events
	 * again.
	 */
	public void freez();

	/**
	 * Lets the event dispatcher publish events again. The events that were in the
	 * queue are the next events. It is possible again to publish events.
	 */
	public void unfreez();

	/**
	 * Clears the current queue with the events. It is still possible to publish
	 * events afterwards, though.
	 */
	public void clearQueue();

	/**
	 * Returns the exception handler that is needed when an exception was thrown
	 * within the dispatching event. Normally, the exception will remain in the
	 * event dispatcher, meaning that the execution won't stop.
	 * 
	 * @return The event handler that should be done when a exception was thrown.
	 */
	public SimulationEventExceptionHandler getExceptionHandler();
}
