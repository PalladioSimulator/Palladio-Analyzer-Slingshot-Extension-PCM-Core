package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;

public class UserWokeUp extends AbstractUserChangedEvent {

	public UserWokeUp(final User entity, final UserInterpretationContext context, final double delay) {
		super(entity, context, delay);
	}

}
