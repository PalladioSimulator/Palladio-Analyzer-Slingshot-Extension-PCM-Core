package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.ActiveResourceRequestContext;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class ActiveResourceRequested extends AbstractEntityChangedEvent<ActiveResourceRequestContext> {

	public ActiveResourceRequested(final ActiveResourceRequestContext entity, final double delay) {
		super(entity, delay);
	}

}
