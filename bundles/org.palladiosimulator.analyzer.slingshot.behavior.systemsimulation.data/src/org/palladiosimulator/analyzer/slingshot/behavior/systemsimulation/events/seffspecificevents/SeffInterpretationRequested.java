package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events.seffspecificevents;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.SeffInterpretationEntity;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.seff.AbstractAction;

public class SeffInterpretationRequested extends AbstractEntityChangedEvent<SeffInterpretationEntity>
        implements SeffInterpretationEvent {

	public SeffInterpretationRequested(final SeffInterpretationEntity entity, final double delay) {
		super(entity, delay);
	}

	/**
	 * Convenience method that creates this new event and automatically generated
	 * the entity. The delay will be set do 0.
	 * 
	 * @param user   The context of the user.
	 * @param action The action as in {@link SeffInterpretationEntity}.
	 * 
	 * @return a new event with the entity containing the {@code action}.
	 */
	public static SeffInterpretationRequested createWithEntity(final AssemblyContext assemblyContext, final User user,
	        final AbstractAction action) {
		final SeffInterpretationEntity entity = new SeffInterpretationEntity(assemblyContext, user, action);
		return new SeffInterpretationRequested(entity, 0);
	}
}
