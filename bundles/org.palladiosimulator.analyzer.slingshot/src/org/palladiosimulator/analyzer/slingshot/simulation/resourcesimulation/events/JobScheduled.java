package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.Job;

public class JobScheduled extends AbstractEntityChangedEvent<Job> {

	public JobScheduled(Job job, double delay) {
		super(job,delay);
	}
	
}
