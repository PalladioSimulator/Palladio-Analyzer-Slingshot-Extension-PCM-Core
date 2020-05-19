package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.User;

public class UserFinished extends UserChangedEvent<User> {
	
	public UserFinished(final User simulatedUser, final UserInterpretationContext userContext) {
		super(simulatedUser, userContext, 0);
	}
}
