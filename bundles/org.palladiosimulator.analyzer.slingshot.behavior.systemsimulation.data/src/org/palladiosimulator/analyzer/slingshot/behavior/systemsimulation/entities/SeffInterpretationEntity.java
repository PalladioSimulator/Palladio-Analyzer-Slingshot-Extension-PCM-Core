package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import org.palladiosimulator.pcm.seff.AbstractAction;

public class SeffInterpretationEntity {

	private final AbstractAction currentAction;

	public SeffInterpretationEntity(final AbstractAction currentAction) {
		super();
		this.currentAction = currentAction;
	}

	public AbstractAction getCurrentAction() {
		return currentAction;
	}

}
