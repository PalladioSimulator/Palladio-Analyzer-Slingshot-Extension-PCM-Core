package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.interpreters;

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

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.User;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserFinished;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserRequestInitiated;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserSlept;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserWokeUp;


public class UsageScenarioInterpreter<T> extends UsagemodelSwitch<T> {

	private final Set<DESEvent> sideEffectEvents;
	private final UserInterpretationContext userContext;
	private final User user;

	private final Logger LOGGER = Logger.getLogger(UsageScenarioInterpreter.class);

	public UsageScenarioInterpreter(User user, UserInterpretationContext context) {
		this.sideEffectEvents = new HashSet<DESEvent>();
		this.userContext = context;
		this.user = user;
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
	public T caseStop(final Stop object) {

		sideEffectEvents.add(new UserFinished(user, userContext));
		return super.caseStop(object);
	}

	@Override
	public T caseEntryLevelSystemCall(EntryLevelSystemCall object) {

		OperationProvidedRole opProvidedRole = EcoreUtil.copy(object.getProvidedRole_EntryLevelSystemCall());	
		OperationSignature signature = EcoreUtil.copy(object.getOperationSignature__EntryLevelSystemCall());
		// Usage Extnesion owns the UserRequestInitiated and is only created by this extension
		// so other extensions can only read/process it.
		
		// if event carries pcm element that are only read only. 
		// MAPEK extension to be the only extension.
		
		
		UserRequestInitiated uRequestInitiated = new UserRequestInitiated(
				new UserRequest(user, opProvidedRole, signature), userContext, 0);		
		sideEffectEvents.add(uRequestInitiated);
		return super.caseEntryLevelSystemCall(object);
	}

	@Override
	public T caseDelay(final Delay object) {

		final double delay = StackContext.evaluateStatic(object.getTimeSpecification_Delay().getSpecification(), Double.class);
		sideEffectEvents.add(new UserSlept(user, userContext.setCurrentAction(object.getSuccessor())));
		sideEffectEvents.add(new UserWokeUp(user, userContext.setCurrentAction(object.getSuccessor()), delay));
		return super.caseDelay(object);
	}

	public Set<DESEvent> continoueInterpretation() {
		this.doSwitch(userContext.getCurrentAction());
		return sideEffectEvents;
	}

	@Override
	public T caseAbstractUserAction(final AbstractUserAction object) {
		return super.caseAbstractUserAction(object);
	}

	public Set<DESEvent> getSideEffectEvents() {
		return sideEffectEvents;

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
	public T caseUsageScenario(final UsageScenario usageScenario) {

		this.doSwitch(usageScenario.getScenarioBehaviour_UsageScenario());

		return super.caseUsageScenario(usageScenario);
	}
}
