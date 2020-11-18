package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.SeffInterpretationEntity;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
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
	 * @param action The action as in {@link SeffInterpretationEntity}.
	 * @return a new event with the entity containing the {@code action}.
	 */
	public static SeffInterpretationRequested withAction(final AbstractAction action) {
		final SeffInterpretationEntity entity = new SeffInterpretationEntity(action);
		return new SeffInterpretationRequested(entity, 0);
	}
}
