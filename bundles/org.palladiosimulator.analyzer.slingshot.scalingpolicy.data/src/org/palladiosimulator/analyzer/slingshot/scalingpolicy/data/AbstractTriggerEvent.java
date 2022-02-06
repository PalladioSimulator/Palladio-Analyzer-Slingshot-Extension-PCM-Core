package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEvent;

/**
 * Signifies that a certain SPD trigger event has happened, i.e. a certain point
 * in simulation time has been reached, or a monitoring behavior constraint has
 * been reached.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractTriggerEvent extends AbstractEvent {

	private final TriggerContext context;

	public AbstractTriggerEvent(final TriggerContext context) {
		this.context = context;
	}

	public TriggerContext getContext() {
		return this.context;
	}

}
