package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.resource.ResourceDemandRequest;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public final class PassiveResourceReleaseRequested extends AbstractEntityChangedEvent<ResourceDemandRequest> implements SEFFInterpreted {

	public PassiveResourceReleaseRequested(final ResourceDemandRequest entity, final double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
