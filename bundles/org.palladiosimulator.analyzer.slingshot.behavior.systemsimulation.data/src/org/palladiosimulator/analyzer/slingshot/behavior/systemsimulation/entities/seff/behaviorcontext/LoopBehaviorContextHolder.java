package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.Optional;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

public final class LoopBehaviorContextHolder extends SingleBehaviorContextHolder {

	private final int maximalLoopCounter;
	private int progression;

	public LoopBehaviorContextHolder(final ResourceDemandingBehaviour behavior, final AbstractAction successor,
			final SeffBehaviorHolder parent, final int maximalLoopCounter) {
		super(behavior, Optional.of(successor), Optional.of(parent));
		this.maximalLoopCounter = maximalLoopCounter;
	}

	@Override
	public boolean hasFinished() {
		return super.hasFinished() && this.progression == this.maximalLoopCounter;
	}

	@Override
	public AbstractAction getNextAction() {
		if (this.getCurrentProcessedBehavior().hasFinished() && this.progression < this.maximalLoopCounter) {
			this.progression++;
			this.getCurrentProcessedBehavior().repeatScenario();
		}
		return super.getNextAction();
	}
}
