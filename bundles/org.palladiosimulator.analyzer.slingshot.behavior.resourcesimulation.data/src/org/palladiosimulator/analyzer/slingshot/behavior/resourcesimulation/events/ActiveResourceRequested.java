package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.ActiveResourceRequestContext;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

/**
 * Specifies an event where an active resource has been requested by someone.
 * This is the entry event of for the resource simulation.
 * 
 * @author Julijan Katic
 */
public class ActiveResourceRequested extends AbstractEntityChangedEvent<ActiveResourceRequestContext> {

	public ActiveResourceRequested(final ActiveResourceRequestContext entity, final double delay) {
		super(entity, delay);
	}

}
