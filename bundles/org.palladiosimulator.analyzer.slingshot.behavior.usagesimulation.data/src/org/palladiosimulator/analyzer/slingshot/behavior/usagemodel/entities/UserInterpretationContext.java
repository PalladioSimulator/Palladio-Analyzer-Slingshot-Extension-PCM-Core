package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

import javax.annotation.processing.Generated;

import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

/**
 * The UserInterpretationContext represents the knowledge that the interpreter
 * needs to continue interpretation for a user.
 * 
 * @author Julijan Katic
 */
public abstract class UserInterpretationContext {

	/** The scenario to interpret. */
	private final UsageScenario scenario;

	/** The current action to be interpreted. */
	private final AbstractUserAction currentAction;

	/** The user of the interpretation. */
	private final User user;

	private final UserLoopContextHolder loopContext;
	private final int currentUsageRun;

	private final UserInterpretationContext parentContext;

	@Generated("SparkTools")
	protected UserInterpretationContext(final BaseBuilder<?, ?> builder) {
		this.scenario = builder.scenario;
		this.currentAction = builder.currentAction;
		this.user = builder.user;
		this.loopContext = builder.loopContext;
		this.currentUsageRun = builder.currentUsageRun;
		this.parentContext = builder.parentContext;
	}

	public UsageScenario getScenario() {
		return scenario;
	}

	public AbstractUserAction getCurrentAction() {
		return currentAction;
	}

	public User getUser() {
		return user;
	}

	public UserInterpretationContext incrementLoopProgression() {
		return this.update().withLoopContext(loopContext.progress()).build();
	}

	public UserLoopContextHolder getUserLoopContextHolder() {
		return this.loopContext;
	}

	public UserInterpretationContext removeLoopContext() {
		return this.update().withLoopContext(null).build();
	}

	public int getCurrentUsageRun() {
		return currentUsageRun;
	}

	public UserInterpretationContext getParentContext() {
		return parentContext;
	}

	public UserInterpretationContext incrementUsageRun() {
		return this.update().withCurrentUsageRun(currentUsageRun + 1).build();
	}
	
	public UserInterpretationContext updateAction(final AbstractUserAction abstractAction) {
		return this.update()
				.withCurrentAction(abstractAction)
				.build();
	}

	public abstract <T extends UserInterpretationContext, B extends BaseBuilder<T, B>> B update();
	
	/**
	 * Helper method to create an update builder. The {@link #update()} should use this method for
	 * to connect the updatable parameters from this parent class.
	 * 
	 * @param <T> The type extending this class. Used to inform which sub-class is built.
	 * @param <B> The type extending the abstract builder class {@link BaseBuilder} for this. Used to indicate which concrete builder class is used.
	 * @param builder The actual concrete builder.
	 * @return The same builder where each parameter of this class is connected to the builder.
	 */
	protected final <T extends UserInterpretationContext, B extends BaseBuilder<T, B>> B updateWithBuilder(final B builder) {
		return builder.withCurrentAction(currentAction)
		        .withCurrentUsageRun(currentUsageRun)
		        .withLoopContext(loopContext)
		        .withScenario(scenario)
		        .withUser(user)
		        .withParentContext(parentContext);
	}

	/**
	 * Builder to build {@link UserInterpretationContext}.
	 */
	@Generated("SparkTools")
	protected static abstract class BaseBuilder<T extends UserInterpretationContext, B extends BaseBuilder<T, B>> {
		private UsageScenario scenario;
		private AbstractUserAction currentAction;
		private User user;
		private UserLoopContextHolder loopContext;
		private int currentUsageRun;
		private UserInterpretationContext parentContext;

		public B withScenario(final UsageScenario scenario) {
			this.scenario = scenario;
			return actualBuilder();
		}

		public B withCurrentAction(final AbstractUserAction currentAction) {
			this.currentAction = currentAction;
			return actualBuilder();
		}

		public B withUser(final User user) {
			this.user = user;
			return actualBuilder();
		}

		public B withLoopContext(final UserLoopContextHolder loopContext) {
			this.loopContext = loopContext;
			return actualBuilder();
		}

		public B withCurrentUsageRun(final int currentUsageRun) {
			this.currentUsageRun = currentUsageRun;
			return actualBuilder();
		}

		public B withParentContext(final UserInterpretationContext parentContext) {
			this.parentContext = parentContext;
			return actualBuilder();
		}
		
		@SuppressWarnings("unchecked")
		protected B actualBuilder() {
			return (B) this;
		}

		public abstract T build();
	}

}
