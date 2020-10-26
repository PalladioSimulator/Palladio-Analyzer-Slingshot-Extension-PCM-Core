package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.Job;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class JobScheduled extends AbstractEntityChangedEvent<Job> {

	public JobScheduled(final Job entity, final double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
