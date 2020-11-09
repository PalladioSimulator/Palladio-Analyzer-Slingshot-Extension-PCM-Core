package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.resources.passive.IPassiveResource;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class ResourceReleased extends AbstractEntityChangedEvent<IPassiveResource> {

	public ResourceReleased(final IPassiveResource entity, final double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
