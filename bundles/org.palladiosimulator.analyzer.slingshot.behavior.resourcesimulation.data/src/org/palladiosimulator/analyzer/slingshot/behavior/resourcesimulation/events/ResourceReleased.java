package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.IResource;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class ResourceReleased extends AbstractEntityChangedEvent<IResource> {

	public ResourceReleased(final IResource entity, final double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
