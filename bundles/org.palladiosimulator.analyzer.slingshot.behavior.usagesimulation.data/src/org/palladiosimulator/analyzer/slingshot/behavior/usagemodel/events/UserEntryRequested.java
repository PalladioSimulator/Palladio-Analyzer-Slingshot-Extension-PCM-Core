package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserRequest;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

/**
 * The user entry request is used to indicate that an {@link EntryLevelSystemCall}
 * is being performed. A system simulation interpreter can use this to simulate
 * the system's repository and SEFFs.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
public final class UserEntryRequested extends AbstractEntityChangedEvent<UserRequest> {
	
	private final UserInterpretationContext userInterpretationContext;

	public UserEntryRequested(UserRequest entity, final UserInterpretationContext context, double delay) {
		super(entity, delay);
		this.userInterpretationContext = context;
	}

	public UserInterpretationContext getUserInterpretationContext() {
		return this.userInterpretationContext;
	}
	
}
