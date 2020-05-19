package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.Job;

public class JobInitiated extends AbstractEntityChangedEvent<Job> {

	public JobInitiated(Job entity, double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
