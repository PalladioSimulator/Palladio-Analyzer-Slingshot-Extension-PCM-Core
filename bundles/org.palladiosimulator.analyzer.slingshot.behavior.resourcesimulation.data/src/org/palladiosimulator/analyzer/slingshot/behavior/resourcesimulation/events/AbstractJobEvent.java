package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.jobs.JobContext;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

/**
 * This abstract class is designed for events that notify a change in the
 * {@link Job} entity.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractJobEvent extends AbstractEntityChangedEvent<JobContext> {

	public AbstractJobEvent(final JobContext entity, final double delay) {
		super(entity, delay);
	}

}