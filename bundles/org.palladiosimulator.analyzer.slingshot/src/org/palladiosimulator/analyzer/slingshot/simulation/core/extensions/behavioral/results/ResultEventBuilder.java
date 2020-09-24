package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral.results;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * A builder class to instanciate a new {@link ResultEvent}.
 * 
 * @param <T> The (super-)type of the events that are saved within the result event.
 * 
 * @author Julijan Katic
 */
public final class ResultEventBuilder<T extends DESEvent> {
	
	private final Set<T> enclosedEvents;
	
	ResultEventBuilder() {
		this.enclosedEvents = new HashSet<>();
	}
	
	/**
	 * Creates the ResultEvent instance.
	 * @return ResultEvent instance.
	 */
	public ResultEvent<T> build() {
		return new ResultEvent<T>(enclosedEvents);
	}
	
	public ResultEventBuilder<T> addEvent(final T event) {
		this.enclosedEvents.add(event);
		return this;
	}
	
	public ResultEventBuilder<T> removeEvent(final T event) {
		this.enclosedEvents.remove(event);
		return this;
	}
	
	public ResultEventBuilder<T> addAll(final Collection<T> events) {
		this.enclosedEvents.addAll(events);
		return this;
	}

}
