package org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobScheduled;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobProgressed;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserStarted;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import com.google.common.eventbus.Subscribe;
import de.uka.ipd.sdq.probfunction.math.util.MathTools;

@OnEvent(eventType = JobProgressed.class, outputEventType = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(eventType = JobFinished.class, outputEventType = DESEvent.class, cardinality = EventCardinality.MANY)
@OnEvent(eventType = UserStarted.class, outputEventType = DESEvent.class, cardinality = EventCardinality.MANY)
public class ResourceSimulationImpl implements SimulationBehaviourExtension {

	
	FCFSResource myFCFSResource = new FCFSResource();
	ProcessorSharingResource myPSResource = new ProcessorSharingResource("test", UUID.randomUUID().toString(),2L);
	
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
	public void init(UsageModel usageModel) {
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
	@Subscribe public ResultEvent<DESEvent> onUserStarted(UserStarted evt) {

		return myPSResource.onUserStarted(evt);
	}

	// the other event-driven trigger of the resource: work leaves
	@Subscribe public ResultEvent<DESEvent> onJobFinished(JobFinished evt) {

		return myPSResource.onJobFinished(evt);
	}

	@Subscribe public ResultEvent<DESEvent> onJobProgressed(JobProgressed evt) {
		return myPSResource.onJobProgressed(evt);
	}

}
