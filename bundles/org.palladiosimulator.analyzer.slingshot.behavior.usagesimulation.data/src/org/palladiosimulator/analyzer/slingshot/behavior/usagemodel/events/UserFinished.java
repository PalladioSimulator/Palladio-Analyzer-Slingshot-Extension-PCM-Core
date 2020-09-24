package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;

public class UserFinished extends AbstractUserChangedEvent {

	public UserFinished(final User entity, final UserInterpretationContext context) {
		super(entity, context, 0);
	}

	

}
