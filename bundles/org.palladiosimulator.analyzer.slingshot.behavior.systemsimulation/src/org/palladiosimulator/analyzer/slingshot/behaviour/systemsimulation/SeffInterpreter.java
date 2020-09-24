package org.palladiosimulator.analyzer.slingshot.behaviour.systemsimulation;

import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.behaviour.systemsimulation.events.RequestFinished;
import org.palladiosimulator.analyzer.slingshot.behaviour.systemsimulation.events.RequestStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.pcm.seff.InternalAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;
import org.palladiosimulator.pcm.seff.util.SeffSwitch;

public class SeffInterpreter<T> extends SeffSwitch<T> {

	private Set<DESEvent> sideEffectEvents;
	private RequestInterpretationContext requestContext;

	private final Logger LOGGER = Logger.getLogger(SeffInterpreter.class);

	@Override
	public T caseInternalAction(final InternalAction object) {
		// TODO Auto-generated method stub
		return super.caseInternalAction(object);
	}

	@Override
	public T caseResourceDemandingBehaviour(final ResourceDemandingBehaviour object) {
		// TODO Auto-generated method stub
		return super.caseResourceDemandingBehaviour(object);
	}

	@Override
	public T caseStartAction(final StartAction object) {
		// TODO Auto-generated method stub
		sideEffectEvents.add(new RequestStarted(requestContext.getRequest(), 0));
		this.doSwitch(object.getSuccessor_AbstractAction());
		return super.caseStartAction(object);
	}

	@Override
	public T caseStopAction(final StopAction object) {
		// TODO Auto-generated method stub
		sideEffectEvents.add(new RequestFinished(requestContext.getRequest()));
		return super.caseStopAction(object);
	}

}
