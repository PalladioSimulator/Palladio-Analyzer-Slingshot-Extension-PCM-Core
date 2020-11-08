package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.IResource;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class ResourceAcquired extends AbstractEntityChangedEvent<IResource> {

	public ResourceAcquired(final IResource entity, final double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
