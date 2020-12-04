package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class UserInterpretationProgressed extends AbstractEntityChangedEvent<UserInterpretationContext> {

	public UserInterpretationProgressed(final UserInterpretationContext entity, final double delay) {
		super(entity, delay);
	}

}
