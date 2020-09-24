package org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation.entities.Job;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class JobInitiated extends AbstractEntityChangedEvent<Job> {

	public JobInitiated(final Job entity, final double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
