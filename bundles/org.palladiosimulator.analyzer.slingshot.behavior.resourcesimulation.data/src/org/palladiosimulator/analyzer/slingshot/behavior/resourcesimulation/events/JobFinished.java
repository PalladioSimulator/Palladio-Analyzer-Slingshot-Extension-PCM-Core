package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.Job;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class JobFinished extends AbstractEntityChangedEvent<Job> {

	public JobFinished(final Job entity, final double delay) {
		super(entity, delay);
	}
	
	
	
}
