package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;


/**
 * The UserInterpretationContext represents the knowledge that the interpreter needs to continoue interpretation for a user
 * 
 * @author Floriment Klinaku
 *
 */
public class UserInterpretationContext {

	public UserInterpretationContext(UsageScenario scenario, AbstractUserAction currentAction) {
		super();
		this.scenario = scenario;
		this.currentAction = currentAction;
	}

	public UsageScenario getScenario() {
		return scenario;
	}

	public void setScenario(UsageScenario scenario) {
		this.scenario = scenario;
	}

	public AbstractUserAction getCurrentAction() {
		return currentAction;
	}

	public UserInterpretationContext setCurrentAction(AbstractUserAction currentAction) {
		return new UserInterpretationContext(scenario, currentAction);
	}

	private UsageScenario scenario;
	private AbstractUserAction currentAction;
	
	public UserInterpretationContext() {
		
	}
}
