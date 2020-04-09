package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.resources;

import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserStarted;


/**
 * IResource represents the interface that every resource has to conform to. It is an event-driven 
 * representation that is neccessary to realize FCFS, PS and other resources. The four operations
 * help implementations to specify how the resource state changes when the simulation is started, 
 * when job arrives (TODO:: replace onUserStarted with onJobScheduled), when job is finished, and 
 * last when job makes progress. 
 * 
 * @author Floriment Klinaku
 *
 */
public interface IResource {

	ResultEvent<DESEvent> onSimulationStarted(SimulationStarted evt);

	ResultEvent<DESEvent> onUserStarted(UserStarted evt);

	ResultEvent<DESEvent> onJobFinished(JobFinished evt);

	ResultEvent<DESEvent> onJobProgressed(JobProgressed evt);

}