package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

/**
 * This abstract class represents an EntityChangedEvent specifically for the {@link User} entity.
 * It also contains a interpretation context.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractUserChangedEvent extends AbstractEntityChangedEvent<User> {

	private final UserInterpretationContext context;
	
	public AbstractUserChangedEvent(final User entity, final UserInterpretationContext context, final double delay) {
		super(entity, delay);
		this.context = context;
	}

	public UserInterpretationContext getUserInterpretationContext() {
		return context;
	}
	
	

}
