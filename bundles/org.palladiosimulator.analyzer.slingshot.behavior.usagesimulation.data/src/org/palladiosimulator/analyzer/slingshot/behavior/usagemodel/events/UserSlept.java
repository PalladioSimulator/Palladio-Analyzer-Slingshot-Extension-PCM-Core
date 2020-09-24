package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;

public class UserSlept extends AbstractUserChangedEvent {

	public UserSlept(final User entity, final UserInterpretationContext context) {
		super(entity, context, 0);
	}

	

}
