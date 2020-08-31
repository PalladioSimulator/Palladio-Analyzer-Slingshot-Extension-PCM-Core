package org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationModel;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.SimulationBehaviourExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.simulation.resourcesimulation.impl.Job;
import org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl.events.RequestFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl.events.RequestInitiated;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserRequestFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserRequestInitiated;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.repository.BasicComponent;

import com.google.common.eventbus.Subscribe;

@OnEvent(when = RequestInitiated.class, then = JobInitiated.class, cardinality =EventCardinality.SINGLE)
@OnEvent(when = RequestFinished.class, then = UserRequestFinished.class, cardinality =EventCardinality.SINGLE)
@OnEvent(when = UserRequestInitiated.class, then = JobInitiated.class, cardinality =EventCardinality.SINGLE)
public class SystemSimulationImpl implements SimulationBehaviourExtension {

	private final Logger LOGGER = Logger.getLogger(SystemSimulationImpl.class);

	private Map<Request, RequestInterpretationContext> requestContexts;
	
	Allocation allocationModel;
	
	// this should be precomputed using the assemblypath. 
	// In simulizar this was computed on the fly based on stacked AssemblyContext
	// so the user while traversing 
	private String requestServerID;
	
	private BasicComponent basicComponent;
	
	// check what is a simulatedbasiccomponent
	
	 // get seffs for call
//    final List<ServiceEffectSpecification> calledSeffs = this
//            .getSeffsForCall(basicComponent.getServiceEffectSpecifications__BasicComponent(), this.signature);

//    final SimulatedStackframe<Object> result = this.interpretSeffs(calledSeffs);

	
	
	@Override
	public void init(final SimulationModel model) {
		// TODO Auto-generated method stub
		allocationModel = model.getAllocation();
		
		requestContexts = new HashMap<Request, RequestInterpretationContext>();
		
		// initialize a single Basic Component depending on the model. 
		
		for (final AllocationContext allocContext : allocationModel.getAllocationContexts_Allocation()) {
			if(allocContext.getId().equals("_b8yfABBfEeqnVv-3S_AxaQ")) {
//				
//				basicComponent = (BasicComponent) allocContext.getAssemblyContext_AllocationContext().getEncapsulatedComponent__AssemblyContext();
//				requestServerID = allocContext.getAssemblyContext_AllocationContext().getId();
//				
//				
//		        final FQComponentID fqID = this.computeFQComponentID();
//		        if (!this.context.getRuntimeState().getComponentInstanceRegistry().hasComponentInstance(fqID)) {
//		            if (LOGGER.isDebugEnabled()) {
//		                LOGGER.debug("Found new basic component component instance, registering it: " + basicComponent);
//		                LOGGER.debug("FQComponentID is " + fqID);
//		            }
//		            this.context.getRuntimeState().getComponentInstanceRegistry()
//		                    .addComponentInstance(new SimulatedBasicComponentInstance(this.context, fqID,
//		                            basicComponent.getPassiveResource_BasicComponent()));
//				
			}
		}

	}
	
	
	// easily react to events produced by another extension. 
	// pre-condition: knowing on the existence of the event in the global tree
	// checking extensions and what events they provide
	@Subscribe public ResultEvent<DESEvent> onUserRequestInitiated(final UserRequestInitiated userRequestInit) {
		
//		userRequestInit.getUserContext() this needs to be returned upon all the request are finished. 
		// -> C -> onRequestFinished -> 
		
		final RequestInterpretationContext requestInterpretationContext = new RequestInterpretationContext(userRequestInit.getUserContext());
		final Request request = new Request();
		requestContexts.put(request, requestInterpretationContext);
		final ResultEvent<DESEvent> result = ResultEvent.of(new JobInitiated(new Job(0, null, false, 10.0, request), 0));
		return result;
	}

	
	@SuppressWarnings("unchecked")
	@Subscribe public ResultEvent<UserRequestFinished> onRequestFinished(final RequestFinished requestFinishedEvt) {
		
		// get the request 
		final Request request = requestFinishedEvt.getEntity();
		final UserRequest userReq = new UserRequest(null, null, null);
		
		
		
		
		return ResultEvent.of(new UserRequestFinished(userReq,requestContexts.get(request).getUserInterpretationContext()));

		// get the ReqeustContext
//		RequestInterpretationContext reqContext = requestContexts.get(request.getId());
		
//		if(reqContext.getParent()==null) {
//			new UserRequestFinished(request, 0);
//		} else {\\
//			continoue interpretation of the parent Request Context.
//		}	
//		return null;
	}
	
	@Subscribe public ResultEvent<JobInitiated> onRequestInitiated(final RequestInitiated requestInitiatedEvt) {
		
		final Request request = requestInitiatedEvt.getEntity();

		return ResultEvent.of(new JobInitiated(new Job(0, null, false, 10.0, request), 0));
		// for a request we need to create a corresponding RequestContext that is passed to the interpreter. 
		
		
//		ProvidedRole providedRole = request.getProvidedRole();
//		Signature signature = request.getSignature();
//		InterfaceProvidingEntity providingEntity = providedRole.getProvidingEntity_ProvidedRole();
////		getSeffsForCall(basicComponent.getServiceEffectSpecifications__BasicComponent(), this.signature);
//
//		if(providingEntity instanceof BasicComponent) {
//			BasicComponent myFirstSimulatedComponent = (BasicComponent) providingEntity;
//	
//		  final List<ServiceEffectSpecification> calledSeffs = this
//	                .getSeffsForCall(myFirstSimulatedComponent.getServiceEffectSpecifications__BasicComponent(), signature);
//
//			// List of SEFFs
//			myFirstSimulatedComponent.getServiceEffectSpecifications__BasicComponent();
//		}
//		
//		return new JobInitiated(new Job(0, null, false, 0, request), 0);
	}

}
