package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.interpreters;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.ResourceDemandRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.SeffInterpretationEntity;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.ResourceDemandRequestInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.SeffInterpretationRequested;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
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

	private final Logger LOGGER = Logger.getLogger(SeffInterpreter.class);

	@Override
	public Set<DESEvent> caseStopAction(final StopAction object) {
		LOGGER.debug("Seff stopped.");
		return Set.of(); // Maybe a SeffInterpretationFinished? TODO
	}

	@Override
	public Set<DESEvent> caseBranchAction(final BranchAction object) {
		// TODO Auto-generated method stub
		return super.caseBranchAction(object);
	}

	@Override
	public Set<DESEvent> caseStartAction(final StartAction object) {
		LOGGER.debug("Found starting action of SEFF");
		return Set.of(
		        new SeffInterpretationRequested(new SeffInterpretationEntity(object.getSuccessor_AbstractAction()), 0));
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
		// TODO Auto-generated method stub
		return Set.of();
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
			final ResourceDemandRequest request = new ResourceDemandRequest(demand,
			        demand.getRequiredResource_ParametricResourceDemand(),
			        demand.getSpecification_ParametericResourceDemand());
			final ResourceDemandRequestInitiated requestEvent = new ResourceDemandRequestInitiated(request, 0);
			events.add(requestEvent);
		}

		if (internalAction.getSuccessor_AbstractAction() != null) {
			events.add(SeffInterpretationRequested.withAction(internalAction.getSuccessor_AbstractAction()));
		}

		return Collections.unmodifiableSet(events);
	}

}
