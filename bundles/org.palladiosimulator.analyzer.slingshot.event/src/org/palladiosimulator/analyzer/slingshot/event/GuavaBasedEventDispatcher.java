package org.palladiosimulator.analyzer.slingshot.event;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Singleton;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.EventDispatcher;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationEventExceptionHandler;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationInterrupted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * This implementation of the event dispatcher uses the Guava's EventBus
 * implementation for dispatching events. The event handler should be annotated
 * with {@link Subscribe}.
 * 
 * @author Julijan Katic
 *
 */
@Singleton
public final class GuavaBasedEventDispatcher implements EventDispatcher {

	private static final Logger LOGGER = Logger.getLogger("EventDispatcher");

	/**
	 * The event bus with a special exception handler. When an exception is thrown
	 * within an event handler, the exception will be catched here and instead the
	 * {@link #getExceptionHandler()} will be called.
	 */
	private EventBus eventBus = new EventBus((exception, context) -> {
		this.getExceptionHandler().onException(exception, (DESEvent) context.getEvent());
	});

	/**
	 * Whether it is possible to publish events or not. If true, it is not possible
	 * to publish further events.
	 */
	private boolean isFreezed = false;

	/**
	 * A copy of the registered handlers, since it is not possible to clear the
	 * queue in Guava's EventBus from the outside.
	 */
	private final Set<Object> handlers = new HashSet<>();

	@Override
	public void post(final Object event) {
		if (!this.isFreezed) {
			this.eventBus.post(event);
		}
	}

	@Override
	public void freez() {
		this.isFreezed = true;
	}

	@Override
	public void unfreez() {
		this.isFreezed = false;
	}

	@Override
	public void clearQueue() {
		this.eventBus = new EventBus();
		this.handlers.forEach(this.eventBus::register);
	}

	@Override
	public void register(final Object object) {
		this.handlers.add(object);
		this.eventBus.register(object);
	}

	@Override
	public void unregister(final Object object) {
		this.eventBus.unregister(object);
		this.handlers.remove(object);
	}

	@Override
	public SimulationEventExceptionHandler getExceptionHandler() {
		return (exception, event) -> {
			LOGGER.error("An exception has been thrown during the event handling", exception);
			this.clearQueue();
			this.post(new SimulationInterrupted(exception));
			this.freez();
		};
	}
}
