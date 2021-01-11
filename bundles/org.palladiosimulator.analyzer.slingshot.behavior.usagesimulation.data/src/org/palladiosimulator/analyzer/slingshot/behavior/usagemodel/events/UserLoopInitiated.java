package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

import com.google.common.base.Preconditions;

public class UserLoopInitiated extends AbstractEntityChangedEvent<UserInterpretationContext> {

	public UserLoopInitiated(final UserInterpretationContext entity) {
		super(entity, 0);
		Preconditions.checkNotNull(entity.getUserLoopContextHolder(), "A user loop must have a loop context");
	}

}
