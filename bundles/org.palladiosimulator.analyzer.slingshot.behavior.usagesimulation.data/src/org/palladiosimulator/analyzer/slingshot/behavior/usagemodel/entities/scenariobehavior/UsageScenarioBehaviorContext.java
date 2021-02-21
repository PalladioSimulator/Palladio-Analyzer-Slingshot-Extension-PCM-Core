package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior;

import java.util.Optional;

import javax.annotation.processing.Generated;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.ScenarioBehaviour;

import com.google.common.base.Preconditions;

/**
 * The UsageScnearioBehaviorContext holds the {@link SceneraioBehaviour} model
 * element which the user is currently walking, as well as other attributes
 * important for interpretation.
 * <p>
 * The UsageScenarioBehaviorContext can reference itself if it has a parent. A
 * parent is defined to be a scenario behavior context in which this context
 * lies. For example, the scenario behavior {@code L} of a loop-action is a
 * child of the root scenario behavior {@code R}, and hence {@code R} is the
 * parent of {@code L}. Using this relationship, the interpreter is able to go
 * back to the actual scenario behavior if the inner scenario behavior is
 * finished.
 * <p>
 * Besides the parent relationship, this context holder contains the
 * {@code nextAction} that appears after the inner scenario behavior. For
 * example, if the action {@code Delay} occurs after the {@code Loop} action,
 * for which this context would point at the scenario behavior, the
 * {@code nextAction} would point to the {@code Delay} action. In this case, the
 * parent would also point to the context which holds the scenario behavior in
 * which the {@code Loop} action is appearing (the root scenario behavior).
 * Hence, the nextAction can only be set if this context is a child context.
 * <p>
 * A scenario behavior might be repeated again for a certain condition.
 * <p>
 * This class is largely <em>sealed</em>, meaning that only a certain set of
 * subclasses are permitted. Current subclasses are
 * <ul>
 * <li>{@link RootScenarioContext}
 * <li>{@link LoopScenarioBehaviorContext}
 * <li>{@link BranchScenarioContext}
 * </ul>
 * 
 * @author Julijan Katic
 * @version 1.0
 *
 */
public abstract class UsageScenarioBehaviorContext {

	private final Optional<AbstractUserAction> nextAction;
	private final Optional<UsageScenarioBehaviorContext> parent;
	private UserInterpretationContext referencedContext;
	private final ScenarioBehaviour scenarioBehavior;

	/**
	 * Instantiates this context using a builder. The builder should be implemented
	 * by the sub-class. If nextAction was set, then parent must be set as well. No
	 * value must be the null reference.
	 * 
	 * @param builder The base builder for this constructor.
	 */
	UsageScenarioBehaviorContext(final BaseBuilder<?, ?> builder) {
		Preconditions.checkArgument(
				builder.nextAction != null && builder.parent != null && builder.scenarioBehavior != null);
		/*
		 * The following precondition checks that (nextAction.isPresent() ==>
		 * parent.isPresent()) which is mathematically equivalent to
		 * (!nextAction.isPresent() || parent.isPresent())
		 */
		Preconditions.checkArgument(builder.nextAction.isEmpty() || builder.parent.isPresent());
		this.nextAction = builder.nextAction;
		this.parent = builder.parent;
		this.referencedContext = builder.referencedContext;
		this.scenarioBehavior = builder.scenarioBehavior;
	}

	/**
	 * Decides whether the this scenario has to be repeated again or not (for
	 * example for "inner" scenarios like loops or branches). I.e. if this is a loop
	 * scenario and the loop counter has not reached the maximum number of loops
	 * yet, the scenario must be repeated.
	 * 
	 * @return true if the scenario has to be repeated again.
	 */
	public abstract boolean mustRepeatScenario();

	/**
	 * Returns the non-{@code null} reference to the next action contained in the
	 * parent context if it is set. If not, then {@link Optional#empty()} will be
	 * returned.
	 * 
	 * If this is set, then {@link #getParent()} will also return a non-empty
	 * reference.
	 * 
	 * @return the optional reference to the next action.
	 */
	public Optional<AbstractUserAction> getNextAction() {
		return this.nextAction;
	}

	/**
	 * Returns the parent context of this context if it was set.
	 * 
	 * @return The parent context of this context.
	 */
	public Optional<UsageScenarioBehaviorContext> getParent() {
		return this.parent;
	}

	/**
	 * Returns whether this context is a child context. This context is defined a
	 * child if {@link #getParent()} is set.
	 * 
	 * @return true if this is a child context.
	 * @see #isRootContext()
	 */
	public boolean isChildContext() {
		return this.parent.isPresent();
	}

	/**
	 * Returns whether this is a root context. The root context is defined if there
	 * is no parent context referenced.
	 * 
	 * @return true if this is a root context
	 * @see #isChildContext()
	 */
	public boolean isRootContext() {
		return this.parent.isEmpty();
	}

	/**
	 * Returns whether this object is fully initialized with non-{@code null}
	 * values.
	 * 
	 * @return true if every reference in this object is not null.
	 */
	public boolean isInitialized() {
		return this.referencedContext != null;
	}

	/**
	 * Returns the {@code non-null} reference to the user interpretation context.
	 * {@link #isInitialized()} must return true in order for this method to work.
	 * 
	 * @return The non-{@code null} reference to the interpretation context.
	 * @throws IllegalStateException if this object is not fully initialized.
	 */
	public UserInterpretationContext getReferencedContext() {
		Preconditions.checkState(this.isInitialized(), "The behavior context is not fully initialized.");
		return this.referencedContext;
	}

	public void updateReferenceContext(final UserInterpretationContext context) {
		this.referencedContext = context;
	}

	/**
	 * This will start the scenario by returning the first user action in this
	 * scenario.
	 * 
	 * @return the first action of that scenario.
	 */
	public AbstractUserAction startScenario() {
		if (!this.mustRepeatScenario()) {
			throw new IllegalStateException("This scenario cannot be repeated again");
		}

		assert !this.scenarioBehavior.getActions_ScenarioBehaviour().isEmpty() : "The list of actions is empty";
		return this.scenarioBehavior.getActions_ScenarioBehaviour().get(0);
	}

	/**
	 * For the preconditions, see
	 * {@link UsageScenarioBehaviorContext#UsageScenarioBehaviorContext}
	 */
	@Generated("SparkTools")
	@SuppressWarnings("unchecked")
	public abstract static class BaseBuilder<T extends UsageScenarioBehaviorContext, B extends BaseBuilder<T, B>> {
		private Optional<AbstractUserAction> nextAction = Optional.empty();
		private Optional<UsageScenarioBehaviorContext> parent = Optional.empty();
		private UserInterpretationContext referencedContext;
		private ScenarioBehaviour scenarioBehavior;

		protected BaseBuilder() {
		}

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
