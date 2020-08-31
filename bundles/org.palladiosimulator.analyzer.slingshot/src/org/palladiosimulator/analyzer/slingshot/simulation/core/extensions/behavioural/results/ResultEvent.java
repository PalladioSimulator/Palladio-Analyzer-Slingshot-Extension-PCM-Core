package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.results;

import java.util.HashSet;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/***
 * ResultEvents is a container of events and offers helper methods to inspect the result. 
 * This eases the checking of the contract. 
 * 
 * @author Floriment Klinaku
 *
 * @param <T> type of contained events
 */
public class ResultEvent<T extends DESEvent> {

	private final Set<T> events;
	
	ResultEvent(final Set<T> evts) {
		events = new HashSet<T>();
		events.addAll(evts);
	}
	
	/**
	 * Returns true iff more than one intermediate events are specified in this result event. It always
	 * returns false iff either {@link #isOne()} or {@link #isEmpty()} are true.
	 */
	public boolean areMany() {
		return events.size() > 1;
	}
	
	/**
	 * Returns true iff there is exactly one immediate event specified in this result event. It always
	 * returns false iff either {@link #areMany()} or {@link #isEmpty()} are true.
	 */
	public boolean isOne() {
		return events.size() == 1;
	}
	
	/**
	 * Returns true iff there are no immediate event specified in this result event. It always
	 * returns false iff either {@link #isOne()} or {@link #areMany()} are true.
	 */
	public boolean isEmpty() {
		return events.isEmpty();
	}
	
	public Set<T> getEventsForScheduling(){
		return events;
	}
	
	/**
	 * Creates the builder class that is used to instantiate a new ResultEvent.
	 * @return a builder class for ResultEvent.
	 */
	public static <T extends DESEvent> ResultEventBuilder<T> createResult() {
		return new ResultEventBuilder<T>();
	}
	
	public static ResultEvent<DESEvent> ofAll(final Set<DESEvent> set) {
		return new ResultEvent<DESEvent>(set);
	}
	
	public static <T extends DESEvent> ResultEvent<T> of(final T event) {
		return new ResultEvent<T>(Set.of(event));
	}
	
	public static ResultEvent<DESEvent> empty() {
		return new ResultEvent<DESEvent>(Set.of());
	}
}
