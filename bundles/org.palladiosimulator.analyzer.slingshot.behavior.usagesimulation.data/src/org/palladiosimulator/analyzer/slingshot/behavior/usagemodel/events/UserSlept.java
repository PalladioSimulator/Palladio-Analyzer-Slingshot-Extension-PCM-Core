package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;

public final class UserSlept extends AbstractUserChangedEvent {

	public UserSlept(final UserInterpretationContext context) {
		super(context, 0);
	}

}
