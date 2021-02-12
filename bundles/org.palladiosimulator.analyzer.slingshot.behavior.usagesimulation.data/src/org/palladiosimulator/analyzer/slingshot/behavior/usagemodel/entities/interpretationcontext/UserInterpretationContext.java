package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext;

import java.util.Optional;

import javax.annotation.processing.Generated;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.UsageScenarioBehaviorContext;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;

import com.google.common.base.Preconditions;

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

	/** The current Run of the usage */
	private final int currentUsageRun;

	/** The parent context */
	private final Optional<UserInterpretationContext> parentContext;

	/** The behavior context indicating in which scenario we are. */
	private final UsageScenarioBehaviorContext behaviorContext;
	
	@Generated("SparkTools")
	protected UserInterpretationContext(final BaseBuilder<?, ?> builder) {
		Preconditions.checkArgument(builder.usageScenarioBehaviorContext != null);
		
		this.scenario = builder.scenario;
		this.currentAction = builder.currentAction;
		this.user = builder.user;
		this.currentUsageRun = builder.currentUsageRun;
		this.parentContext = builder.parentContext;
		this.behaviorContext = builder.usageScenarioBehaviorContext;
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

	public int getCurrentUsageRun() {
		return currentUsageRun;
	}

	public Optional<UserInterpretationContext> getParentContext() {
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
		return builder.withCurrentAction(this.currentAction)
		        .withCurrentUsageRun(this.currentUsageRun)
		        .withScenario(this.scenario)
		        .withUser(this.user)
		        .withParentContext(this.parentContext)
		        .withUsageScenarioBehaviorContext(getBehaviorContext());
	}
	public UsageScenarioBehaviorContext getBehaviorContext() {
		return behaviorContext;
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
		private Optional<UserInterpretationContext> parentContext = Optional.empty();
		private UsageScenarioBehaviorContext usageScenarioBehaviorContext;

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

		public B withParentContext(final Optional<UserInterpretationContext> parentContext) {
			this.parentContext = parentContext;
			return actualBuilder();
		}
		
		public B withUsageScenarioBehaviorContext(final UsageScenarioBehaviorContext usageScenarioBehaviorContext) {
			this.usageScenarioBehaviorContext = usageScenarioBehaviorContext;
			return actualBuilder();
		}
		
		@SuppressWarnings("unchecked")
		protected B actualBuilder() {
			return (B) this;
		}

		public abstract T build();
	}

}
