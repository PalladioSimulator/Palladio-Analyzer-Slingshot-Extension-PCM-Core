package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results;

import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * @author Floriment Klinaku
 *
 * @param <T> the type of events contained in the ManyEvents
 */
public class ManyEvents<T extends DESEvent> extends Result {
	
	private final Set<T> events;
	
	public ManyEvents(Set<T> events) {
		this.events = events;
	}
	
	public Set<T> getManyEvents(){
		return events;
	}


}
