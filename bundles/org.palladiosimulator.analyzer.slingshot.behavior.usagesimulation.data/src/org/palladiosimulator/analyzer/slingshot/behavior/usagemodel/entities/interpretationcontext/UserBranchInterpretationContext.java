package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior.GeneralScenarioBehaviorContext;
import org.palladiosimulator.pcm.usagemodel.Start;
import javax.annotation.processing.Generated;

/**
 * A context holder for branches.
 * 
 * @author Julijan Katic
 * @version 1.0
 */
public class UserBranchInterpretationContext {
	
	private final GeneralScenarioBehaviorContext userBranchScenarioBehavior;
	private final UserInterpretationContext userInterpretationContext;
	private final Start startAction;
	
	@Generated("SparkTools")
	private UserBranchInterpretationContext(Builder builder) {
		this.userBranchScenarioBehavior = builder.userBranchScenarioBehavior;
		this.userInterpretationContext = builder.userInterpretationContext;
		this.startAction = builder.startAction;
	}
	
	public GeneralScenarioBehaviorContext getUserBranchScenarioBehavior() {
		return userBranchScenarioBehavior;
	}
	public UserInterpretationContext getUserInterpretationContext() {
		return userInterpretationContext;
	}
	public Start getStartAction() {
		return startAction;
	}
	
	/**
	 * Creates builder to build {@link UserBranchInterpretationContext}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}
	/**
	 * Builder to build {@link UserBranchInterpretationContext}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private GeneralScenarioBehaviorContext userBranchScenarioBehavior;
		private UserInterpretationContext userInterpretationContext;
		private Start startAction;

		private Builder() {
		}

		public Builder withUserBranchScenarioBehavior(GeneralScenarioBehaviorContext userBranchScenarioBehavior) {
			this.userBranchScenarioBehavior = userBranchScenarioBehavior;
			return this;
		}

		public Builder withUserInterpretationContext(UserInterpretationContext userInterpretationContext) {
			this.userInterpretationContext = userInterpretationContext;
			return this;
		}

		public Builder withStartAction(Start startAction) {
			this.startAction = startAction;
			return this;
		}

		public UserBranchInterpretationContext build() {
			return new UserBranchInterpretationContext(this);
		}
	}
	
	
}
