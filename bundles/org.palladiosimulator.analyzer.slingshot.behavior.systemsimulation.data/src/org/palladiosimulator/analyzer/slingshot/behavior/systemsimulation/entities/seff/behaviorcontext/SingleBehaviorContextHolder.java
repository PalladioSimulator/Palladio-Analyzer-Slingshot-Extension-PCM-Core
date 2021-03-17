package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.List;
import java.util.Optional;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

public abstract class SingleBehaviorContextHolder extends SeffBehaviorContextHolder {

	protected SingleBehaviorContextHolder(final ResourceDemandingBehaviour behavior,
			final Optional<AbstractAction> successor, final Optional<SeffBehaviorHolder> parent) {
		super(List.of(behavior), successor, parent);
	}

	@Override
	public SeffBehaviorHolder getCurrentProcessedBehavior() {
		return this.getBehaviors().get(0);
	}

}
