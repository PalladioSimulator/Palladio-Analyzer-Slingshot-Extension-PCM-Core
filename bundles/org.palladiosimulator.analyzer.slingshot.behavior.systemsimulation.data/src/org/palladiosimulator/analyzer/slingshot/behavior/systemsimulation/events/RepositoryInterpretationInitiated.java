package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.RepositoryInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class RepositoryInterpretationInitiated extends AbstractEntityChangedEvent<RepositoryInterpretationContext> {

	public RepositoryInterpretationInitiated(final RepositoryInterpretationContext entity, final double delay) {
		super(entity, delay);
	}

}
