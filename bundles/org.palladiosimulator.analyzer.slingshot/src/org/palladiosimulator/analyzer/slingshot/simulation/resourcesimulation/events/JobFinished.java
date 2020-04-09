package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.Job;

public class JobFinished extends AbstractEntityChangedEvent<Job> {
	
	public JobFinished(Job job, double delay) {
		super(job, delay);
	}

}
