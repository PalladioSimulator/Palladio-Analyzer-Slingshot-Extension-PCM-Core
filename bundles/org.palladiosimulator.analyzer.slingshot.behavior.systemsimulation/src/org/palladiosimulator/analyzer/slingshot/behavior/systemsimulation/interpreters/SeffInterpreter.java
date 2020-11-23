package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.GeneralEntryRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.ResourceDemandRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RequestFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.RequestInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.seffspecificevents.ResourceDemandRequestInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.seffspecificevents.SeffInterpretationRequested;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.seff.AcquireAction;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.CollectionIteratorAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ForkAction;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.LoopAction;
import org.palladiosimulator.pcm.seff.ReleaseAction;
import org.palladiosimulator.pcm.seff.SetVariableAction;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.palladiosimulator.pcm.seff.seff_performance.ParametricResourceDemand;
import org.palladiosimulator.pcm.seff.util.SeffSwitch;

/**
 * The interpreter uses a certain {@code Switch} (like the Visitor Pattern) to
 * iterate through the elements in the Seff model and interpret certain model
 * elements.
 * 
 * It generates new events and returns them on each visit.
 * 
 * @author Julijan Katic
 */
public class SeffInterpreter extends SeffSwitch<Set<DESEvent>> {

	private static final Logger LOGGER = Logger.getLogger(SeffInterpreter.class);

	private final User userContext;
	private final AssemblyContext assemblyContext;

	public SeffInterpreter(final AssemblyContext context, final User userContext) {
		this.userContext = userContext;
		this.assemblyContext = context;
	}

	@Override
	public Set<DESEvent> caseStopAction(final StopAction object) {
		LOGGER.debug("Seff stopped.");
		return Set.of(new RequestFinished(userContext));
	}

	@Override
	public Set<DESEvent> caseBranchAction(final BranchAction object) {
		// TODO Auto-generated method stub
		return super.caseBranchAction(object);
	}

	@Override
	public Set<DESEvent> caseStartAction(final StartAction object) {
		LOGGER.debug("Found starting action of SEFF");
		return Set.of(SeffInterpretationRequested.createWithEntity(assemblyContext, userContext,
		        object.getSuccessor_AbstractAction()));
	}

	@Override
	public Set<DESEvent> caseReleaseAction(final ReleaseAction object) {
		// TODO Auto-generated method stub
		return super.caseReleaseAction(object);
	}

	@Override
	public Set<DESEvent> caseLoopAction(final LoopAction object) {
		// TODO Auto-generated method stub
		return super.caseLoopAction(object);
	}

	@Override
	public Set<DESEvent> caseForkAction(final ForkAction object) {
		// TODO Auto-generated method stub
		return super.caseForkAction(object);
	}

	@Override
	public Set<DESEvent> caseExternalCallAction(final ExternalCallAction externalCall) {
		final OperationRequiredRole requiredRole = externalCall.getRole_ExternalService();
		final OperationSignature calledServiceSignature = externalCall.getCalledService_ExternalService();
		final EList<VariableUsage> inputVariableUsages = externalCall.getInputVariableUsages__CallAction();

		final GeneralEntryRequest request = new GeneralEntryRequest(userContext, requiredRole, calledServiceSignature,
		        inputVariableUsages);

		return Set.of(new RequestInitiated(request, 0));
	}

	@Override
	public Set<DESEvent> caseAcquireAction(final AcquireAction object) {
		// TODO Auto-generated method stub
		return super.caseAcquireAction(object);
	}

	@Override
	public Set<DESEvent> caseCollectionIteratorAction(final CollectionIteratorAction object) {
		// TODO Auto-generated method stub
		return super.caseCollectionIteratorAction(object);
	}

	@Override
	public Set<DESEvent> caseSetVariableAction(final SetVariableAction object) {
		// TODO Auto-generated method stub
		return super.caseSetVariableAction(object);
	}

	@Override
	public Set<DESEvent> caseInternalAction(final InternalAction internalAction) {
		LOGGER.debug("Found internal action");
		final Set<DESEvent> events = new HashSet<>();
		final EList<ParametricResourceDemand> resourceDemandAction = internalAction.getResourceDemand_Action();

		for (final ParametricResourceDemand demand : resourceDemandAction) {
			LOGGER.debug("Demand found with: " + demand);
			final ResourceDemandRequest request = new ResourceDemandRequest(assemblyContext, userContext, demand,
			        demand.getRequiredResource_ParametricResourceDemand(),
			        demand.getSpecification_ParametericResourceDemand());
			final ResourceDemandRequestInitiated requestEvent = new ResourceDemandRequestInitiated(request, 0);
			events.add(requestEvent);
		}

		if (internalAction.getSuccessor_AbstractAction() != null) {
			events.add(
			        SeffInterpretationRequested.createWithEntity(assemblyContext, userContext,
			                internalAction.getSuccessor_AbstractAction()));
		}

		return Collections.unmodifiableSet(events);
	}

	@Override
	public Set<DESEvent> doSwitch(final EClass eClass, final EObject eObject) {
		Set<DESEvent> result = super.doSwitch(eClass, eObject);
		if (result == null) {
			result = Set.of();
		}
		return result;
	}

}
