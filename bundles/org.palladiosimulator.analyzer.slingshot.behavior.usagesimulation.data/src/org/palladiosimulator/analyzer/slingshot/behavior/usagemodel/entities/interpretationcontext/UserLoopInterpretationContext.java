package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.GeneralScenarioBehaviorContext;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;

import javax.annotation.processing.Generated;

public class UserLoopInterpretationContext {

	private final GeneralScenarioBehaviorContext userLoopScenarioBehavior;
	
	private final UserInterpretationContext userInterpretationContext;
	
	private final int maximalLoopCount;
	
	private final int progression;
	
	private final boolean loopFinished;
	
	private final AbstractUserAction startAction;

	@Generated("SparkTools")
	private UserLoopInterpretationContext(Builder builder) {
		this.userLoopScenarioBehavior = builder.userLoopScenarioBehavior;
		this.userInterpretationContext = builder.userInterpretationContext;
		this.maximalLoopCount = builder.maximalLoopCount;
		this.progression = builder.progression;
		this.startAction = builder.startAction;
		this.loopFinished = this.progression >= this.maximalLoopCount;
	}

	public GeneralScenarioBehaviorContext getUserLoopScenarioBehavior() {
		return userLoopScenarioBehavior;
	}

	public UserInterpretationContext getUserInterpretationContext() {
		return userInterpretationContext;
	}

	public int getMaximalLoopCount() {
		return maximalLoopCount;
	}

	public int getProgression() {
		return progression;
	}

	public boolean isLoopFinished() {
		return loopFinished;
	}

	public AbstractUserAction getStartAction() {
		return startAction;
	}
	
	public Builder update() {
		return builder().withMaximalLoopCount(maximalLoopCount)
					    .withProgression(progression)
					    .withUserInterpretationContext(userInterpretationContext)
					    .withUserLoopScenarioBehavior(userLoopScenarioBehavior);
	}

	public UserLoopInterpretationContext progress() {
		return this.update().withProgression(progression + 1).build();
	}
	
	/**
	 * Creates builder to build {@link UserLoopInterpretationContext}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link UserLoopInterpretationContext}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private GeneralScenarioBehaviorContext userLoopScenarioBehavior;
		private UserInterpretationContext userInterpretationContext;
		private int maximalLoopCount;
		private int progression;
		private AbstractUserAction startAction;

		private Builder() {
		}

		public Builder withUserLoopScenarioBehavior(GeneralScenarioBehaviorContext userLoopScenarioBehavior) {
			this.userLoopScenarioBehavior = userLoopScenarioBehavior;
			return this;
		}

		public Builder withUserInterpretationContext(UserInterpretationContext userInterpretationContext) {
			this.userInterpretationContext = userInterpretationContext;
			return this;
		}

		public Builder withMaximalLoopCount(int maximalLoopCount) {
			this.maximalLoopCount = maximalLoopCount;
			return this;
		}

		public Builder withProgression(int progression) {
			this.progression = progression;
			return this;
		}
		
		public Builder withStartAction(final AbstractUserAction startAction) {
			this.startAction = startAction;
			return this;
		}

		public UserLoopInterpretationContext build() {
			return new UserLoopInterpretationContext(this);
		}
	}
	
	
}
