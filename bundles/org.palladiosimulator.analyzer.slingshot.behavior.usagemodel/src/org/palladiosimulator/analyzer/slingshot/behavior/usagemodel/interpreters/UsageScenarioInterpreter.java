package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.interpreters;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserRequestInitiated;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserSlept;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events.UserWokeUp;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.ProvidesEvents;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.pcm.repository.OperationProvidedRole;
import org.palladiosimulator.pcm.repository.OperationSignature;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.Delay;
import org.palladiosimulator.pcm.usagemodel.EntryLevelSystemCall;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;
import org.palladiosimulator.pcm.usagemodel.Start;
import org.palladiosimulator.pcm.usagemodel.Stop;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.palladiosimulator.pcm.usagemodel.util.UsagemodelSwitch;

import de.uka.ipd.sdq.simucomframework.variables.StackContext;

public class UsageScenarioInterpreter<T> extends UsagemodelSwitch<T> {
	
	private final Logger LOGGER = Logger.getLogger(UsageScenarioInterpreter.class);
	
	private final Set<DESEvent> sideEffectEvents;
	private final UserInterpretationContext userContext;
	private final User user;
	
	@SuppressWarnings("unchecked")
	public UsageScenarioInterpreter(final User user, final UserInterpretationContext userContext) {
		super();
		
		this.sideEffectEvents = new HashSet<>();
		this.userContext = userContext;
		this.user = user;
	}
	
	public Set<DESEvent> continueInterpretation() {
		this.doSwitch(userContext.getCurrentAction());
		return this.getSideEffectEvents();
	}
	
	@ProvidesEvents({
		UserRequestInitiated.class,
		UserFinished.class, UserStarted.class,
		UserSlept.class, UserWokeUp.class
	})
	public Set<DESEvent> getSideEffectEvents() {
		return sideEffectEvents;
	}

	@Override
	public T caseEntryLevelSystemCall(final EntryLevelSystemCall object) {
		final OperationProvidedRole opProvidedRole = EcoreUtil.copy(object.getProvidedRole_EntryLevelSystemCall());
		final OperationSignature signature = EcoreUtil.copy(object.getOperationSignature__EntryLevelSystemCall());
		
		final UserRequest userRequest = new UserRequest(user, opProvidedRole, signature);
		final UserRequestInitiated uRequestInitiated = new UserRequestInitiated(userRequest, userContext, 0);
		sideEffectEvents.add(uRequestInitiated);
		return super.caseEntryLevelSystemCall(object);
	}

	@Override
	public T caseStop(final Stop object) {
		sideEffectEvents.add(new UserFinished(user, userContext));
		return super.caseStop(object);
	}

	@Override
	public T caseStart(final Start object) {
		sideEffectEvents.add(new UserStarted(user, userContext));
		
		if (object.getSuccessor() != null) {
			this.doSwitch(object.getSuccessor());
		}
		
		return super.caseStart(object);
	}

	@Override
	public T caseUsageScenario(final UsageScenario object) {
		this.doSwitch(object.getScenarioBehaviour_UsageScenario());
		return super.caseUsageScenario(object);
	}

	@Override
	public T caseAbstractUserAction(final AbstractUserAction object) {
		return super.caseAbstractUserAction(object);
	}

	@Override
	public T caseScenarioBehaviour(final ScenarioBehaviour object) {
		// interpret start user action
		for (final AbstractUserAction abstractUserAction : object.getActions_ScenarioBehaviour()) {
			if (abstractUserAction instanceof Start) {
				this.doSwitch(abstractUserAction);
				break;
			}
		}
		return super.caseScenarioBehaviour(object);
	}

	@Override
	public T caseDelay(final Delay object) {
		final double delay = StackContext.evaluateStatic(object.getTimeSpecification_Delay().getSpecification(), Double.class);
		sideEffectEvents.add(new UserSlept(user, userContext.setCurrentAction(object.getSuccessor())));
		sideEffectEvents.add(new UserWokeUp(user, userContext.setCurrentAction(object.getSuccessor()), delay));
		return super.caseDelay(object);
	}

	
}
