package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserInterpretationContext;

public class UserFinished extends AbstractUserChangedEvent {

	public UserFinished(final UserInterpretationContext context) {
		super(context, 0);
	}

}
