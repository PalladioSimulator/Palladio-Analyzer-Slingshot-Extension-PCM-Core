package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.resources;

import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

/**
 * IResource represents the interface that every resource has to conform to. It
 * is an event-driven representation that is necessary to realize FCFS, PS and
 * other resources. The four operations help implementations to specify how the
 * resource state changes when the simulation is started, when job arrives
 * (TODO:: replace onUserStarted with onJobSecheduled), when job is finished and
 * lastly when job makes progress.
 * 
 * @author Floriment Klinaku
 */
public interface IResourceHandler {

	ResultEvent<DESEvent> onJobProgressed(JobProgressed evt);
}
