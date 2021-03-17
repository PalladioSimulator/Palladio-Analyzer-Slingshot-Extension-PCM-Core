package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;

public abstract class SeffBehaviorContextHolder implements BehaviorContextProxy {

	private final List<SeffBehaviorHolder> behaviors;
	private final Optional<AbstractAction> successor;
	private final Optional<SeffBehaviorHolder> parent;

	protected SeffBehaviorContextHolder(final List<ResourceDemandingBehaviour> behaviors,
			final Optional<AbstractAction> successor, final Optional<SeffBehaviorHolder> parent) {
		this.behaviors = behaviors.stream()
				.map(behavior -> new SeffBehaviorHolder(behavior, this))
				.collect(Collectors.toList());
		this.successor = successor;
		this.parent = parent;
	}

	@Override
	public Optional<AbstractAction> getSuccessor() {
		return this.successor;
	}

	@Override
	public boolean hasFinished() {
		return !this.behaviors.stream().anyMatch(holder -> !holder.hasFinished());
	}

	@Override
	public Optional<SeffBehaviorHolder> getParent() {
		return this.parent;
	}

	@Override
	public AbstractAction getNextAction() {
		if (this.hasFinished()) {
			throw new IllegalStateException();
		}
		return this.getCurrentProcessedBehavior().next();
	}

	protected List<SeffBehaviorHolder> getBehaviors() {
		return this.behaviors;
	}

}
