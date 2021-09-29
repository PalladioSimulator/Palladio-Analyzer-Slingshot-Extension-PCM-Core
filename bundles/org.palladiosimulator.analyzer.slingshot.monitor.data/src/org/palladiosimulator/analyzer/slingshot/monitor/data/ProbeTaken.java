package org.palladiosimulator.analyzer.slingshot.monitor.data;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public final class ProbeTaken extends AbstractEntityChangedEvent<ProbeTakenEntity> {

	public ProbeTaken(final ProbeTakenEntity entity) {
		super(entity, 0);
	}

}
