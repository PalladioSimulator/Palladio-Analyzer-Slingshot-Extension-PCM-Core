package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

/**
 * The UserInterpretationContext represents the knowledge that the interpreter
 * needs to continue interpretation for a user.
 * 
 * @author Julijan Katic
 */
public class UserInterpretationContext {

	private UsageScenario scenario;
	private final AbstractUserAction currentAction;
	private User user;

	public UserInterpretationContext(final UsageScenario scenario, final AbstractUserAction currentAction) {
		this.scenario = scenario;
		this.currentAction = currentAction;
	}

	public UsageScenario getScenario() {
		return scenario;
	}

	public void setScenario(final UsageScenario scenario) {
		this.scenario = scenario;
	}

	public AbstractUserAction getCurrentAction() {
		return currentAction;
	}

	public UserInterpretationContext setCurrentAction(final AbstractUserAction currentAction) {
		return new UserInterpretationContext(scenario, currentAction);
	}

	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

}
