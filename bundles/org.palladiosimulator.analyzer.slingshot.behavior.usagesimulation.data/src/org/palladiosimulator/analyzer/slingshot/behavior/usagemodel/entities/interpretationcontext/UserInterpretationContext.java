package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext;

import java.util.Optional;

import javax.annotation.processing.Generated;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.GeneralScenarioBehaviorContext;
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
	
	private final GeneralScenarioBehaviorContext scenarioContext;

	/** The current action to be interpreted. */
	private final AbstractUserAction currentAction;

	/** The user of the interpretation. */
	private final User user;

	/** The current Run of the usage */
	private final int currentUsageRun;

	/** The parent context */
	private final Optional<UserInterpretationContext> parentContext;

	private final Optional<UserLoopInterpretationContext> currentLoopInterpretationContext;
	
	private final Optional<UserBranchInterpretationContext> currentBranchInterpretationContext;
	
	@Generated("SparkTools")
	protected UserInterpretationContext(final BaseBuilder<?, ?> builder) {
		this.scenario = builder.scenario;
		this.currentAction = builder.currentAction;
		this.user = builder.user;
		this.currentUsageRun = builder.currentUsageRun;
		this.parentContext = Optional.ofNullable(builder.parentContext);
		this.currentLoopInterpretationContext = Optional.ofNullable(builder.currentLoopInterpretationContext);
		this.currentBranchInterpretationContext = Optional.ofNullable(builder.currentBranchInterpretationContext);
		this.scenarioContext = new GeneralScenarioBehaviorContext(scenario.getScenarioBehaviour_UsageScenario(), null, null);
	}

	public UsageScenario getScenario() {
		return scenario;
	}
	
	public GeneralScenarioBehaviorContext getScenarioContext() {
		return this.scenarioContext;
	}

	public AbstractUserAction getCurrentAction() {
		return currentAction;
	}

	public User getUser() {
		return user;
	}

	public UserInterpretationContext removeLoopContext() {
		return this.update()
				.withUserLoopInterpretationContext(null)
				.build();
	}

	public int getCurrentUsageRun() {
		return currentUsageRun;
	}

	public Optional<UserInterpretationContext> getParentContext() {
		return parentContext;
	}

	public Optional<UserLoopInterpretationContext> getCurrentLoopInterpretationContext() {
		return currentLoopInterpretationContext;
	}

	public Optional<UserBranchInterpretationContext> getCurrentBranchInterpretationContext() {
		return currentBranchInterpretationContext;
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
		return builder.withCurrentAction(this.currentAction)
		        .withCurrentUsageRun(this.currentUsageRun)
		        .withUserLoopInterpretationContext(this.currentLoopInterpretationContext.orElse(null))
		        .withScenario(this.scenario)
		        .withUser(this.user)
		        .withParentContext(this.parentContext.orElse(null));
	}
	/**
	 * Builder to build {@link UserInterpretationContext}.
	 */
	@Generated("SparkTools")
	protected static abstract class BaseBuilder<T extends UserInterpretationContext, B extends BaseBuilder<T, B>> {
		private UsageScenario scenario;
		private AbstractUserAction currentAction;
		private User user;
		private int currentUsageRun;
		private UserInterpretationContext parentContext;
		private UserLoopInterpretationContext currentLoopInterpretationContext;
		private UserBranchInterpretationContext currentBranchInterpretationContext;

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

		public B withCurrentUsageRun(final int currentUsageRun) {
			this.currentUsageRun = currentUsageRun;
			return actualBuilder();
		}

		public B withParentContext(final UserInterpretationContext parentContext) {
			this.parentContext = parentContext;
			return actualBuilder();
		}
		
		public B withUserLoopInterpretationContext(final UserLoopInterpretationContext currentLoopInterpretationContext) {
			this.currentLoopInterpretationContext = currentLoopInterpretationContext;
			return actualBuilder();
		}
		
		public B withCurrentBranchInterpretationContext(final UserBranchInterpretationContext currentBranchInterpretationContext) {
			this.currentBranchInterpretationContext = currentBranchInterpretationContext;
			return actualBuilder();
		}
		
		@SuppressWarnings("unchecked")
		protected B actualBuilder() {
			return (B) this;
		}

		public abstract T build();
	}

}
