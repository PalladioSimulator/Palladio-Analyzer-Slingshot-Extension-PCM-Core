package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserInterpretationContext;

public class UserSlept extends AbstractUserChangedEvent {

	public UserSlept(final UserInterpretationContext context) {
		super(context, 0);
	}

}
