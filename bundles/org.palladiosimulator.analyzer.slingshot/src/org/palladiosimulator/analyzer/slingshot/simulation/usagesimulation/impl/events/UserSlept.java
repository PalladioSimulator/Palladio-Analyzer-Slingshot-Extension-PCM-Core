package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.*;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.User;

public class UserSlept extends UserChangedEvent<User> {

//	private final double timeToSleep;

	public UserSlept(final User user, final UserInterpretationContext userContext) {
		super(user,userContext,0);
//		this.timeToSleep = timeToSleep;
	}

}
