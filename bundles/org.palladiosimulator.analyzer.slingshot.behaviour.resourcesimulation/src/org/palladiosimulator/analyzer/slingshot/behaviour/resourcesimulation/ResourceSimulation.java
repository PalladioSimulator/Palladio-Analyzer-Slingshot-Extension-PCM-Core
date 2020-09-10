package org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation.entities.FCFSResource;
import org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation.entities.IResource;
import org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation.entities.ProcessorSharingResource;
import org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behaviour.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationModel;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;

import com.google.common.eventbus.Subscribe;

@OnEvent(when = SimulationStarted.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = JobProgressed.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = JobFinished.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(when = JobFinished.class, then = DESEvent.class, cardinality = EventCardinality.MANY)
public class ResourceSimulation implements SimulationBehaviourExtension {

	IResource myFCFSResource = new FCFSResource();
	IResource myPSResource = new ProcessorSharingResource("test", UUID.randomUUID().toString(), 2L);

	private final Logger LOGGER = Logger.getLogger(ResourceSimulation.class);

	// all these should belong to a registry
	// TODO:: the Resource Container ID is container in the Request and its base is
	// the PCM spec.
	// so the extension creating the Request and putting as an event in the bus
	// processes also the resource env to translate
	// between the service request and the actual resource.

	// maps (ResourceContainer ID, ResourceType ID) -> SimActiveResource
	// private Map<String, SimActiveResource> containerToResourceMap;
	public ResourceSimulation() {

	}

	@Override
	public void init(final SimulationModel model) {
		final Allocation allocation = model.getAllocation();
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
