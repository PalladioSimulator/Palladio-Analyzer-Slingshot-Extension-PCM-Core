package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.SEFFInterpretationContext;
import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext.RootBehaviorContextHolder;

public final class SEFFChildInterpretationStarted extends AbstractSEFFInterpretationEvent {

	public SEFFChildInterpretationStarted(final SEFFInterpretationContext context) {
		super(context, 0);
		if (context.getBehaviorContext() instanceof RootBehaviorContextHolder) {
			throw new IllegalArgumentException("The child interpretator must have a parent context.");
		}
	}
	
}
