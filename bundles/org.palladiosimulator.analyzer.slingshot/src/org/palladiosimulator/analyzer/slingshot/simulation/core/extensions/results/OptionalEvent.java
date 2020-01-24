package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results;

import java.util.Optional;
import java.util.Set;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * @author Floriment Klinaku
 *
 * @param <T> the type of DESEvent contained in the Optional
 */
public class OptionalEvent<T extends DESEvent> extends Result {

	private final Optional<T> optionalEvent;
	
	public OptionalEvent(T evt) {
		this.optionalEvent = Optional.of(evt);
	}

	public Optional<T> getOptionalEvent() {
		return optionalEvent;
	}
	
	@Override
	public Set<DESEvent> getEventsForScheduling(){
		if (optionalEvent.isPresent()) {
			return Set.of(optionalEvent.get());
		}
		return Set.of();
	}
}
