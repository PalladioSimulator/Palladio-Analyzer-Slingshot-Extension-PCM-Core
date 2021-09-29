package org.palladiosimulator.analyzer.slingshot.simulation.core.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public final class HandlerInterrupted extends AbstractEntityChangedEvent<Throwable> {

	public HandlerInterrupted(final Throwable entity) {
		super(entity, 0);
	}

}
