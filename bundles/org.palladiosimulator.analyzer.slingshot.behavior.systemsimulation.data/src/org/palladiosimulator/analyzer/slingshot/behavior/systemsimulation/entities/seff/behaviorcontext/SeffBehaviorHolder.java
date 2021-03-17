package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.seff.behaviorcontext;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.ResourceDemandingBehaviour;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;

public final class SeffBehaviorHolder implements Iterator<AbstractAction> {

	private final ResourceDemandingBehaviour behavior;
	private final SeffBehaviorContextHolder context;
	private AbstractAction currentAction;

	public SeffBehaviorHolder(final ResourceDemandingBehaviour behavior, final SeffBehaviorContextHolder context) {
		super();
		this.behavior = behavior;
		this.context = context;
		this.currentAction = behavior.getSteps_Behaviour().get(0);
	}

	/**
	 * @return the behavior
	 */
	public ResourceDemandingBehaviour getBehavior() {
		return this.behavior;
	}

	/**
	 * @return the currentAction
	 */
	public AbstractAction getCurrentAction() {
		return this.currentAction;
	}
	
	public SeffBehaviorContextHolder getContext() {
		return this.context;
	}

	public boolean hasFinished() {
		return this.currentAction instanceof StopAction;
	}

	public void repeatScenario() {
		this.currentAction = this.behavior.getSteps_Behaviour().stream()
				.filter(StartAction.class::isInstance)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("This model does not have a start action."));
	}

	@Override
	public boolean hasNext() {
		return !this.hasFinished() && this.currentAction.getSuccessor_AbstractAction() != null;
	}

	@Override
	public AbstractAction next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException("There is no action after StopAction");
		}
		final AbstractAction result = this.currentAction;
		this.currentAction = this.currentAction.getSuccessor_AbstractAction();
		return result;
	}

}
