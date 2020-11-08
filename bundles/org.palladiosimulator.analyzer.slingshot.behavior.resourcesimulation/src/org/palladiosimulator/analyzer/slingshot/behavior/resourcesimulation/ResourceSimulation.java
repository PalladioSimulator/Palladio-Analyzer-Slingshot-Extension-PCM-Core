package org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation;

import java.util.UUID;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.FCFSResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.IResourceHandler;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.ProcessorSharingResource;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;

import com.google.common.eventbus.Subscribe;

@OnEvent(when = SimulationStarted.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = JobProgressed.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = JobFinished.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = JobInitiated.class, then = {}, cardinality = EventCardinality.MANY)
public class ResourceSimulation implements SimulationBehaviorExtension {

	IResourceHandler myFCFSResource = new FCFSResource();
	IResourceHandler myPSResource = new ProcessorSharingResource("test", UUID.randomUUID().toString(), 2L);

	private final Logger LOGGER = Logger.getLogger(ResourceSimulation.class);

	private final Allocation allocation;

	@Inject
	public ResourceSimulation(final Allocation allocation) {
		this.allocation = allocation;
	}

	@Override
	public void init() {
		final System system = allocation.getSystem_Allocation();
		final ResourceEnvironment resourceEnvironmentModel = allocation.getTargetResourceEnvironment_Allocation();

		resourceEnvironmentModel.getResourceContainer_ResourceEnvironment();

		LOGGER.info(String.format("Primitive FCFS Single Resource Simulation initialized"));
	}

	@Subscribe
	public ResultEvent<DESEvent> onSimulationStarted(final SimulationStarted evt) {
		// activate resouces
		// we could create a tuple of next processing finished and also that a user
		// request is finished.
		// new ProcessingFinished(delay) -> next
		// new UserFinished(now)
		return myPSResource.onSimulationStarted(evt);
	}

	// the event-driven variant of doProcessing: work arrives for processing
	@Subscribe
	public ResultEvent<DESEvent> onJobInitiated(final JobInitiated evt) {

		return myPSResource.onJobInitiated(evt);
	}

	// the other event-driven trigger of the resource: work leaves
	@Subscribe
	public ResultEvent<DESEvent> onJobFinished(final JobFinished evt) {

		return myPSResource.onJobFinished(evt);
	}

	@Subscribe
	public ResultEvent<DESEvent> onJobProgressed(final JobProgressed evt) {
		return myPSResource.onJobProgressed(evt);
	}
}
