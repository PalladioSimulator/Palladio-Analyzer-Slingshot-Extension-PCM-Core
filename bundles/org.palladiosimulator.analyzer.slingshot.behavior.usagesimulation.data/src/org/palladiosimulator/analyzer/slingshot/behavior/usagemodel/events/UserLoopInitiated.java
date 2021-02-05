package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserLoopInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;


public final class UserLoopInitiated extends AbstractEntityChangedEvent<UserLoopInterpretationContext> {

	public UserLoopInitiated(final UserLoopInterpretationContext entity) {
		super(entity, 0);
	}

}
