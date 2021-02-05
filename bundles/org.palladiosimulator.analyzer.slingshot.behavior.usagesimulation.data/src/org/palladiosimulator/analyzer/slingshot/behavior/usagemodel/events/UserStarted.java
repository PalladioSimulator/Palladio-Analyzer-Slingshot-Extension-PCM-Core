package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;

public final class UserStarted extends AbstractUserChangedEvent {

	public UserStarted(final UserInterpretationContext context, final double delay) {
		super(context, delay);
	}

	public UserStarted(final UserInterpretationContext context) {
		this(context, 0);
	}

}
