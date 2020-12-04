package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.Job;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

/**
 * Specifies that the job has been finished and can be safely removed from the
 * list.
 * 
 * @author Julijan Katic
 */
public class JobFinished extends AbstractEntityChangedEvent<Job> {

	public JobFinished(final Job entity, final double delay) {
		super(entity, delay);
	}

	public JobFinished(final Job entity) {
		this(entity, 0);
	}

}
