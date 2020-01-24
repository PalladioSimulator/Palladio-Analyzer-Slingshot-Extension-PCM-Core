package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * @author Floriment Klinaku
 *
 * @param <T> the type of event contained in the SingleEvent result
 */
public class SingleEvent<T extends DESEvent> extends Result {
	
	private final T evt;
	
	public SingleEvent(T evt) {
		this.evt = evt;
	}

	public T getEvent() {
		return evt;
	}
	
	@Override
	public Set<DESEvent> getEventsForScheduling(){
		return Set.of(evt);
	}
}
