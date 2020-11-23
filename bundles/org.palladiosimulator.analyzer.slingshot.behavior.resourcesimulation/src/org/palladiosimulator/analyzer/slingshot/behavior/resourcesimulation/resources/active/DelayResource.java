package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.active;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources.IResourceHandler;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

/**
 * A delay resource is a special active resource that represents an infinite
 * amount of resource usage.
 * 
 * @author Julijan Katic
 */
public class DelayResource implements IResourceHandler {

	@Override
	public ResultEvent<DESEvent> onJobProgressed(final JobProgressed evt) {
		// TODO Auto-generated method stub
		return ResultEvent.empty();
	}

}
