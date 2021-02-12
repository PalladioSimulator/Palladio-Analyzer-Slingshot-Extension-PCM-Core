package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;

public class InnerScenarioBehaviorInitiated extends AbstractUserChangedEvent {

	public InnerScenarioBehaviorInitiated(UserInterpretationContext entity, double delay) {
		super(entity, delay);
	}

}
