package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl;

import java.util.UUID;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationModel;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.resources.FCFSResource;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.resources.IResource;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.resources.ProcessorSharingResource;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserStarted;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.system.System;
import com.google.common.eventbus.Subscribe;

@OnEvent(eventType = JobProgressed.class, outputEventType = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(eventType = JobFinished.class, outputEventType = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(eventType = JobInitiated.class, outputEventType = DESEvent.class, cardinality = EventCardinality.MANY)
public class ResourceSimulationImpl implements SimulationBehaviourExtension {

	
	IResource myFCFSResource = new FCFSResource();
	IResource myPSResource = new ProcessorSharingResource("test", UUID.randomUUID().toString(),2L);
	
	private final Logger LOGGER = Logger.getLogger(ResourceSimulationImpl.class);

	
	// all these should  belong to a registry
	// TODO:: the Resource Container ID is container in the Request and its base is the PCM spec.
	// so the extension creating the Request and putting as an event in the bus processes also the resource env to translate 
	// between the service request and the actual resource.
	
    // maps (ResourceContainer ID, ResourceType ID) -> SimActiveResource
	// private Map<String, SimActiveResource> containerToResourceMap;

	public ResourceSimulationImpl() {

	}

	@Override
	public void init(final SimulationModel model) {
		
		Allocation allocation = model.getAllocation();
		System system = allocation.getSystem_Allocation();
		ResourceEnvironment resourceEnvironmentModel = allocation.getTargetResourceEnvironment_Allocation();
		
		resourceEnvironmentModel.getResourceContainer_ResourceEnvironment();
		
		// TODO Auto-generated method stub
		// Resource Environment Model 
		LOGGER.info(String.format("Primitive FCFS Single ResourceSimulation initialized"));
		

	}

	@Subscribe public ResultEvent<DESEvent> onSimulationStarted(SimulationStarted evt) {
		// activate resouces
		// we could create a tuple of next processing finished and also that a user
		// request is finished.
		// new ProcessingFinished(delay) -> next
		// new UserFinished(now)
		return myPSResource.onSimulationStarted(evt);
	}

	// the event-driven variant of doProcessing: work arrives for processing
	@Subscribe public ResultEvent<DESEvent> onJobInitiated(JobInitiated evt) {
		
		return myPSResource.onJobInitiated(evt);
	}

	// the other event-driven trigger of the resource: work leaves
	@Subscribe public ResultEvent<DESEvent> onJobFinished(JobFinished evt) {

		return myPSResource.onJobFinished(evt);
	}

	@Subscribe public ResultEvent<DESEvent> onJobProgressed(JobProgressed evt) {
		return myPSResource.onJobProgressed(evt);
	}

}
