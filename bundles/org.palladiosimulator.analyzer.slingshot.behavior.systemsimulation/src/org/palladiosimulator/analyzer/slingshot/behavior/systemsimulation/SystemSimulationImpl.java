package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.entities.Job;
import org.palladiosimulator.analyzer.slingshot.behavior.resourcesimulation.events.JobInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.Request;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RequestFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RequestInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestInitiated;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.SimulationBehaviorExtension;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.entity.InterfaceProvidingEntity;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;

import com.google.common.eventbus.Subscribe;

@OnEvent(when = RequestInitiated.class, then = JobInitiated.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = RequestFinished.class, then = UserRequestFinished.class, cardinality = EventCardinality.SINGLE)
@OnEvent(when = UserRequestInitiated.class, then = JobInitiated.class, cardinality = EventCardinality.SINGLE)
public class SystemSimulationImpl implements SimulationBehaviorExtension {

	private final Logger LOGGER = Logger.getLogger(SystemSimulationImpl.class);

	private Map<Request, RequestInterpretationContext> requestContexts;

	private final Allocation allocationModel;

	// this should be precomputed using the assemblypath.
	// In simulizar this was computed on the fly based on stacked AssemblyContext
	// so the user while traversing
	private String requestServerID;

	private BasicComponent basicComponent;

	@Inject
	public SystemSimulationImpl(final Allocation allocationModel) {
		this.allocationModel = allocationModel;
	}

	// check what is a simulatedbasiccomponent

	// get seffs for call
//	    final List<ServiceEffectSpecification> calledSeffs = this
//	            .getSeffsForCall(basicComponent.getServiceEffectSpecifications__BasicComponent(), this.signature);

//	    final SimulatedStackframe<Object> result = this.interpretSeffs(calledSeffs);

	@Override
	public void init() {
		// TODO Auto-generated method stub

		requestContexts = new HashMap<Request, RequestInterpretationContext>();

		// initialize a single Basic Component depending on the model.

		for (final AllocationContext allocContext : allocationModel.getAllocationContexts_Allocation()) {
			if (allocContext.getId().equals("_b8yfABBfEeqnVv-3S_AxaQ")) {
//					
//					basicComponent = (BasicComponent) allocContext.getAssemblyContext_AllocationContext().getEncapsulatedComponent__AssemblyContext();
//					requestServerID = allocContext.getAssemblyContext_AllocationContext().getId();
//					
//					
//			        final FQComponentID fqID = this.computeFQComponentID();
//			        if (!this.context.getRuntimeState().getComponentInstanceRegistry().hasComponentInstance(fqID)) {
//			            if (LOGGER.isDebugEnabled()) {
//			                LOGGER.debug("Found new basic component component instance, registering it: " + basicComponent);
//			                LOGGER.debug("FQComponentID is " + fqID);
//			            }
//			            this.context.getRuntimeState().getComponentInstanceRegistry()
//			                    .addComponentInstance(new SimulatedBasicComponentInstance(this.context, fqID,
//			                            basicComponent.getPassiveResource_BasicComponent()));
//					
			}
		}

	}

	// easily react to events produced by another extension.
	// pre-condition: knowing on the existence of the event in the global tree
	// checking extensions and what events they provide
	@Subscribe
	public ResultEvent<DESEvent> onUserRequestInitiated(final UserRequestInitiated userRequestInit) {

//			userRequestInit.getUserContext() this needs to be returned upon all the request are finished. 
		// -> C -> onRequestFinished ->

		final RequestInterpretationContext requestInterpretationContext = new RequestInterpretationContext(
		        userRequestInit.getUserContext());
		final Request request = new Request();
		requestContexts.put(request, requestInterpretationContext);

		return ResultEvent.of(new JobInitiated(new Job(0, null, false, 10.0, request), 0));
	}

	@SuppressWarnings("unchecked")
	@Subscribe
	public ResultEvent<UserRequestFinished> onRequestFinished(final RequestFinished requestFinishedEvt) {

		// get the request
		final Request request = requestFinishedEvt.getEntity();
		final UserRequest userReq = new UserRequest(null, null, null);

		return ResultEvent
		        .of(new UserRequestFinished(userReq, requestContexts.get(request).getUserInterpretationContext()));

		// get the ReqeustContext
//			RequestInterpretationContext reqContext = requestContexts.get(request.getId());

//			if(reqContext.getParent()==null) {
//				new UserRequestFinished(request, 0);
//			} else {\\
//				continoue interpretation of the parent Request Context.
//			}	
//			return null;
	}

	@Subscribe
	public ResultEvent<JobInitiated> onRequestInitiated(final RequestInitiated requestInitiatedEvt) {

		final Request request = requestInitiatedEvt.getEntity();

		// return ResultEvent.of(new JobInitiated(new Job(0, null, false, 10.0,
		// request), 0));
		// for a request we need to create a corresponding RequestContext that is passed
		// to the interpreter.

		final ProvidedRole providedRole = request.getProvidedRole();
		final Signature signature = request.getSignature();
		final InterfaceProvidingEntity providingEntity = providedRole.getProvidingEntity_ProvidedRole();
//		getSeffsForCall(basicComponent.getServiceEffectSpecifications__BasicComponent(), this.signature);
//
		if (providingEntity instanceof BasicComponent) {
			final BasicComponent myFirstSimulatedComponent = (BasicComponent) providingEntity;

			final List<ServiceEffectSpecification> calledSeffs = this
			        .getSeffsForCall(myFirstSimulatedComponent.getServiceEffectSpecifications__BasicComponent(),
			                signature);
//
//			// List of SEFFs
//			myFirstSimulatedComponent.getServiceEffectSpecifications__BasicComponent();
		}

		return ResultEvent.of(new JobInitiated(new Job(0, null, false, 0, request), 0));
	}

	private List<ServiceEffectSpecification> getSeffsForCall(
	        final EList<ServiceEffectSpecification> serviceEffectSpecifications, final Signature signature) {
		// TODO Auto-generated method stub
		return null;
	}
}
