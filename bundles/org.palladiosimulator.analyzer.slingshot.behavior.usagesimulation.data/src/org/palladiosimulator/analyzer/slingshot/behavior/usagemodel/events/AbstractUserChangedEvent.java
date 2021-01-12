package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.events;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

/**
 * This abstract class represents an EntityChangedEvent specifically for the
 * {@link User} entity. It also contains a interpretation context.
 * 
 * @author Julijan Katic
 */
public abstract class AbstractUserChangedEvent extends AbstractEntityChangedEvent<UserInterpretationContext>
        implements UsageInterpretationEvent {

	public AbstractUserChangedEvent(final UserInterpretationContext entity, final double delay) {
		super(entity, delay);
	}

}
