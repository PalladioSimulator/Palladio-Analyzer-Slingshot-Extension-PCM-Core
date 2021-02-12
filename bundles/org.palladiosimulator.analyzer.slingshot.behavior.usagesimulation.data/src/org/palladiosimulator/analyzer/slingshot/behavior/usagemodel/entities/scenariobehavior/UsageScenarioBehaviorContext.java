package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior;

import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;

import com.google.common.base.Preconditions;

public abstract class UsageScenarioBehaviorContext {

	private final Optional<AbstractUserAction> nextAction;
	private final Optional<UsageScenarioBehaviorContext> parent;
	private final UserInterpretationContext referencedContext;
	private final ScenarioBehaviour scenarioBehavior;
	
	protected UsageScenarioBehaviorContext(final BaseBuilder<?, ?> builder) {
		Preconditions.checkArgument(builder.nextAction != null && builder.parent != null && builder.referencedContext != null);
		/* The following precondition checks that (nextAction.isPresent() ==> parent.isPresent())
		 * which is mathematically equivalent to (!nextAction.isPresent() || parent.isPresent()) */
		Preconditions.checkArgument(builder.nextAction.isEmpty() || builder.parent.isPresent());
		this.nextAction = builder.nextAction;
		this.parent = builder.parent;
		this.referencedContext = builder.referencedContext.update()
				.withUsageScenarioBehaviorContext(this)
				.build();
		this.scenarioBehavior = builder.scenarioBehavior;
	}
	
	/**
	 * Decides whether the this scenario has to be repeated again or not
	 * (for example for "inner" scenarios like loops or branches). I.e.
	 * if this is a loop scenario and the loop counter has not reached
	 * the maximum number of loops yet, the scenario must be repeated.
	 * 
	 * @return true if the scenario has to be repeated again.
	 */
	public abstract boolean mustRepeatScenario();
	
	public Optional<AbstractUserAction> getNextAction() {
		return this.nextAction;
	}
	
	public Optional<UsageScenarioBehaviorContext> getParent() {
		return this.parent;
	}
	
	/**
	 * This will start the scenario by returning the first user action
	 * in this scenario. 
	 * @return the first action of that scenario.
	 */
	public AbstractUserAction startScenario() {
		if (!this.mustRepeatScenario()) {
			throw new IllegalStateException("This scenario cannot be repeated again");
		}
		
		assert !scenarioBehavior.getActions_ScenarioBehaviour().isEmpty() : "The list of actions is empty";
		return scenarioBehavior.getActions_ScenarioBehaviour().get(0);
	}
	
	public UserInterpretationContext getReferencedContext() {
		return referencedContext;
	}

	public static abstract class BaseBuilder<T extends UsageScenarioBehaviorContext, B extends BaseBuilder<T, B>> {
		private Optional<AbstractUserAction> nextAction;
		private Optional<UsageScenarioBehaviorContext> parent;
		private UserInterpretationContext referencedContext;
		private ScenarioBehaviour scenarioBehavior;
		
		protected BaseBuilder() {}
		
		public B withNextAction(final Optional<AbstractUserAction> nextAction) {
			this.nextAction = nextAction;
			return (B) this;
		}
		
		public B withParent(final Optional<UsageScenarioBehaviorContext> parent) {
			this.parent = parent;
			return (B) this;
		}
		
		public B withReferencedContext(final UserInterpretationContext referencedContext) {
			this.referencedContext = referencedContext;
			return (B) this;
		}
		
		public B withScenarioBehavior(final ScenarioBehaviour scenarioBehavior) {
			this.scenarioBehavior = scenarioBehavior;
			return (B) this;
		}
		
		public abstract T build();
	}
}
