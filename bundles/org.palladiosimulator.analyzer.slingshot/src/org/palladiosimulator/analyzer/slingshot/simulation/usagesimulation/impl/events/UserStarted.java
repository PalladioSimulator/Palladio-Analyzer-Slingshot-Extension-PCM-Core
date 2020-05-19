package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.User;

public class UserStarted extends UserChangedEvent<User> {

	

	public UserStarted(final User simulatedUser, final UserInterpretationContext userCtx) {
		super(simulatedUser, userCtx, 0);
	}


}
