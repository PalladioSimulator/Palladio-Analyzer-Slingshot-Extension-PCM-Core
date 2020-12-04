package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel;

import javax.annotation.processing.Generated;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.UserLoopContextHolder;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

/**
 * The UserInterpretationContext represents the knowledge that the interpreter
 * needs to continue interpretation for a user.
 * 
 * @author Julijan Katic
 */
public class UserInterpretationContext {

	/** The scenario to interpret. */
	private final UsageScenario scenario;

	/** The current action to be interpreted. */
	private final AbstractUserAction currentAction;

	/** The user of the interpretation. */
	private final User user;
	private final double thinkTime;

	private final UserLoopContextHolder loopContext;
	private final int currentUsageRun;

	private final UserInterpretationContext parentContext;

	@Generated("SparkTools")
	private UserInterpretationContext(final Builder builder) {
		this.scenario = builder.scenario;
		this.currentAction = builder.currentAction;
		this.user = builder.user;
		this.thinkTime = builder.thinkTime;
		this.loopContext = builder.loopContext;
		this.currentUsageRun = builder.currentUsageRun;
		this.parentContext = builder.parentContext;
	}

	public UserInterpretationContext(final UsageScenario scenario, final AbstractUserAction currentAction) {
		this(scenario, currentAction, 0);
	}

	public UserInterpretationContext(final UsageScenario scenario, final AbstractUserAction currentAction,
	        final double thinkTime) {
		this(builder().withScenario(scenario)
		        .withCurrentAction(currentAction)
		        .withThinkTime(thinkTime)
		        .withUser(new User()));
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

	public double getThinkTime() {
		return thinkTime;
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

	public Builder update() {
		return builder().withCurrentAction(currentAction)
		        .withCurrentUsageRun(currentUsageRun)
		        .withLoopContext(loopContext)
		        .withScenario(scenario)
		        .withThinkTime(thinkTime)
		        .withUser(user)
		        .withParentContext(parentContext);
	}

	/**
	 * Creates builder to build {@link UserInterpretationContext}.
	 * 
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link UserInterpretationContext}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private UsageScenario scenario;
		private AbstractUserAction currentAction;
		private User user;
		private double thinkTime;
		private UserLoopContextHolder loopContext;
		private int currentUsageRun;
		private UserInterpretationContext parentContext;

		private Builder() {
		}

		public Builder withScenario(final UsageScenario scenario) {
			this.scenario = scenario;
			return this;
		}

		public Builder withCurrentAction(final AbstractUserAction currentAction) {
			this.currentAction = currentAction;
			return this;
		}

		public Builder withUser(final User user) {
			this.user = user;
			return this;
		}

		public Builder withThinkTime(final double thinkTime) {
			this.thinkTime = thinkTime;
			return this;
		}

		public Builder withLoopContext(final UserLoopContextHolder loopContext) {
			this.loopContext = loopContext;
			return this;
		}

		public Builder withCurrentUsageRun(final int currentUsageRun) {
			this.currentUsageRun = currentUsageRun;
			return this;
		}

		public Builder withParentContext(final UserInterpretationContext parentContext) {
			this.parentContext = parentContext;
			return this;
		}

		public UserInterpretationContext build() {
			return new UserInterpretationContext(this);
		}
	}

}
