package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results;

import java.util.Optional;

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
}
