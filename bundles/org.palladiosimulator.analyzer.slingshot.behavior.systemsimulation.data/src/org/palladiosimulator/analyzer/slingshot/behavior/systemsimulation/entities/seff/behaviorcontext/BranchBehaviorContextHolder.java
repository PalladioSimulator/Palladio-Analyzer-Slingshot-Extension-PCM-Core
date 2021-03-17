package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.Optional;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

public final class BranchBehaviorContextHolder extends SingleBehaviorContextHolder {

	public BranchBehaviorContextHolder(final ResourceDemandingBehaviour behavior, final AbstractAction successor,
			final SeffBehaviorHolder parent) {
		super(behavior, Optional.of(successor), Optional.of(parent));
	}

}
