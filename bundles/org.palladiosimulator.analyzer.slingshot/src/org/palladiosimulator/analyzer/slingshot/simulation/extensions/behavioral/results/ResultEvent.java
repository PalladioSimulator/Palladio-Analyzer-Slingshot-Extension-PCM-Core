package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/***
 * ResultEvents is a container of events and offers helper methods to inspect
 * the result. This eases the checking of the contract.
 * <p>
 * This container holds an <em>ordered</em> set of events that can be later put
 * into the event processor. The ordering can be important, because event
 * processors typically process events in <em>pre-order</em>: If A and B are two
 * events, and A is inserted into the set before B, then A will be processed and
 * published first. All the resulting events from B are then instantly inserted
 * afterwards <strong>before</strong> B is published. Only if no event occurs
 * anymore resulting from A, then B is processed.
 * 
 * @param <T> The upper bound of event types.
 * 
 * @author Floriment Klinaku
 * @author Julijan Katic
 */
public class ResultEvent<T extends DESEvent> {

	/** The set of events that will be dispatched. */
	private final Set<T> events;

	/**
	 * Constructs a result set. This will copy {@code evts} and respects the
	 * insertion order.
	 * 
	 * @param evts The set of events to copy.
	 */
	public ResultEvent(final Set<T> evts) {
		this.events = new LinkedHashSet<T>(evts);
	}

	/**
	 * Constructs a result set without resulting events.
	 */
	public ResultEvent() {
		this.events = Set.of();
	}

	/**
	 * Returns true iff more than one intermediate events are specified in this
	 * result event. It always returns false iff either {@link #isOne()} or
	 * {@link #isEmpty()} are true.
	 */
	public boolean areMany() {
		return this.events.size() > 1;
	}

	/**
	 * Returns true iff there is exactly one immediate event specified in this
	 * result event. It always returns false iff either {@link #areMany()} or
	 * {@link #isEmpty()} are true.
	 */
	public boolean isOne() {
		return this.events.size() == 1;
	}

	/**
	 * Returns true iff there are no immediate event specified in this result event.
	 * It always returns false iff either {@link #isOne()} or {@link #areMany()} are
	 * true.
	 */
	public boolean isEmpty() {
		return this.events.isEmpty();
	}

	/**
	 * Returns an ordered, but unmodifiable set of events.
	 * 
	 * @return the set of events.
	 */
	public Set<T> getEventsForScheduling() {
		return Collections.unmodifiableSet(this.events);
	}

	/**
	 * Adds multiple events into the set existing set and returns a new instance
	 * with all the events.
	 * 
	 * @param <S>    The upper bound of event types.
	 * @param events The events to be added.
	 * @return new instance containing the existing and new events.
	 */
	public <S extends T> ResultEvent<T> and(final S... events) {
		if (events == null) {
			return this;
		}
		return this.and(Set.of(events));
	}

	/**
	 * Merges the ResultEvent's set and the new set together and returns a new
	 * instance with the merged set.
	 * 
	 * @param <S>    The upper bound of the inserting event types.
	 * @param events The set of events to be merged.
	 * @return new instance containing the merged set.
	 */
	public <S extends T> ResultEvent<T> and(final Set<S> events) {
		if (events == null || events.isEmpty()) {
			return this;
		} else {
			final Set<T> eventSets = new LinkedHashSet<>(this.events);
			eventSets.addAll(events);
			return new ResultEvent<>(eventSets);
		}
	}

	/**
	 * Directly creates a ResultEvent from a set of events. Null values will be
	 * disregarded when instantiating. Calling this method will {@code null} values
	 * will have the same effect as if calling it with an empty set.
	 * 
	 * @param events the set of events. Null values will be disregarded if contained
	 *               in the set.
	 * @return a new instance of ResultEvent.
	 */
	public static <T extends DESEvent> ResultEvent<T> ofAll(final Set<T> events) {
		final Set<T> actualEvents;
		if (events == null) {
			actualEvents = Set.of();
		} else {
			actualEvents = events.stream()
					.filter(event -> event != null)
					.collect(Collectors.toSet());
		}
		return new ResultEvent<T>(actualEvents);
	}

	/**
	 * Returns a new ResultEvent instance from a variable number of events. Null
	 * values will be ignored when instantiating.
	 * 
	 * @param events list of events. Null values will be ignored.
	 * @return new instance of ResultEvent containing these events.
	 */
	@SafeVarargs
	public static <T extends DESEvent> ResultEvent<T> of(final T... events) {
		if (events == null) {
			return ResultEvent.empty();
		} else {
			return ResultEvent.of(Arrays.stream(events)
					.filter(event -> event != null)
					.collect(Collectors.toSet()));
		}
	}

	/**
	 * Synonym for {@link #ofAll(Set)}.
	 * 
	 * @see #ofAll(Set)
	 */
	public static <T extends DESEvent> ResultEvent<T> of(final Set<T> events) {
		return ofAll(events);
	}

	/**
	 * Creates a ResultEvent instance with no events.
	 */
	public static <T extends DESEvent> ResultEvent<T> empty() {
		return new ResultEvent<T>(Set.of());
	}
}
