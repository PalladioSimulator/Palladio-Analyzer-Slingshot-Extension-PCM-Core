package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results;

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

	private Set<T> events;
	private boolean optional;

	
	public ResultEvent(Set<T> evts) {
		events = new HashSet<T>();
		events.addAll(evts);
	}
	
	public boolean areMany() {
		return events.size() > 1;
	}
	
	public boolean isOne() {
		return events.size() == 1;
	}
	
	public boolean isEmpty() {
		return events.isEmpty();
	}
	
	public Set<T> getEventsForScheduling(){
		return events;
	}
	
	
}
