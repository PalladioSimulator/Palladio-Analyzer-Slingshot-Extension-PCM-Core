package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public final class UserInterpretationProgressed extends AbstractEntityChangedEvent<UserInterpretationContext> {

	public UserInterpretationProgressed(final UserInterpretationContext entity, final double delay) {
		super(entity, delay);
	}

}
