package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

/**
 * An event holding the {@link ResourceDemandRequest} entity specifying that a
 * certain resource should is requested. This typically is fired by a
 * InternalAction of a SEFF.
 * 
 * @author Julijan Katic
 */
public class ResourceDemandRequestInitiated extends AbstractEntityChangedEvent<ResourceDemandRequest>
        implements SEFFInterpreted {

	public ResourceDemandRequestInitiated(final ResourceDemandRequest entity, final double delay) {
		super(entity, delay);
	}

	public ResourceDemandRequestInitiated(final ResourceDemandRequest entity) {
		this(entity, 0);
	}
	
}
