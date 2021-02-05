package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserBranchInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public final class UserBranchInitiated extends AbstractEntityChangedEvent<UserBranchInterpretationContext> {

	public UserBranchInitiated(final UserBranchInterpretationContext entity) {
		super(entity, 0);
	}

}
