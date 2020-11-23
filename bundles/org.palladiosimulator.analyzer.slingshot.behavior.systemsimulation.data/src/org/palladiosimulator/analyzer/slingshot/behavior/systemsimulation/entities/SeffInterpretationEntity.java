package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.seff.AbstractAction;

public class SeffInterpretationEntity extends SeffContextHolderEntity {

	private final AbstractAction currentAction;

	public SeffInterpretationEntity(final AssemblyContext assemblyContext, final User user,
	        final AbstractAction currentAction) {
		super(assemblyContext, user);
		this.currentAction = currentAction;
	}

	public AbstractAction getCurrentAction() {
		return currentAction;
	}

}
