package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

public final class ForkBehaviorContextHolder extends MultiBehaviorContextHolder {

	public int currentBehaviorIndex = 0;

	public ForkBehaviorContextHolder(final List<ResourceDemandingBehaviour> behaviors, final AbstractAction successor,
			final SeffBehaviorHolder parent) {
		super(behaviors, Optional.of(successor), Optional.of(parent));
	}

	@Override
	public SeffBehaviorHolder getCurrentProcessedBehavior() {
		final SeffBehaviorHolder holder = this.getBehaviors().get(this.currentBehaviorIndex);
		SeffBehaviorHolder nextHolder;

		do {
			this.currentBehaviorIndex++;
			nextHolder = this.getBehaviors().get(this.currentBehaviorIndex);
		} while (nextHolder.hasFinished());

		return holder;
	}

}
