package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.ActiveResourceRequestContext;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

/**
 * Specifies that the request has been successfully processed and the job is
 * done.
 * 
 * @author Julijan Katic
 */
public class ActiveResourceFinished extends AbstractEntityChangedEvent<ActiveResourceRequestContext> {

	public ActiveResourceFinished(final ActiveResourceRequestContext entity, final double delay) {
		super(entity, delay);
	}

}
