package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;

public class UserWokeUp extends AbstractUserChangedEvent {

	public UserWokeUp(final UserInterpretationContext context, final double delay) {
		super(context, delay);
	}

}
