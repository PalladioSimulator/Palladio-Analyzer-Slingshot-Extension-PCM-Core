package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.Optional;

import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

public final class RootBehaviorContextHolder extends SingleBehaviorContextHolder {

	public RootBehaviorContextHolder(final ResourceDemandingBehaviour behavior) {
		super(behavior, Optional.empty(), Optional.empty());
	}

}
