package org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation.entities;

import org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
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
public interface IResource {

	ResultEvent<DESEvent> onSimulationStarted(SimulationStarted evt);

	ResultEvent<DESEvent> onJobInitiated(JobInitiated evt);

	ResultEvent<DESEvent> onJobFinished(JobFinished evt);

	ResultEvent<DESEvent> onJobProgressed(JobProgressed evt);
}
