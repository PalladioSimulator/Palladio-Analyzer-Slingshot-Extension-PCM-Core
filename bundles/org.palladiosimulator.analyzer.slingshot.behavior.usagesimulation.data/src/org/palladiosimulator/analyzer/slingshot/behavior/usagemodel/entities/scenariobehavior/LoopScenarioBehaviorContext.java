package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.scenariobehavior;

import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.interpretationcontext.UserInterpretationContext;
import org.palladiosimulator.pcm.usagemodel.AbstractUserAction;

import com.google.common.base.Preconditions;

public class LoopScenarioBehaviorContext extends UsageScenarioBehaviorContext {
	
	private final int maximalLoopCount;
	private int progression;

	public LoopScenarioBehaviorContext(final Builder builder) {
		super(builder);
		Preconditions.checkArgument(this.getNextAction().isPresent(), "The next action must be present");
		Preconditions.checkArgument(builder.initialLoopCount < builder.maximalLoopCount && builder.initialLoopCount >= 0);
		
		this.maximalLoopCount = builder.maximalLoopCount;
		this.progression = builder.initialLoopCount;
	}
	
	@Override
	public boolean mustRepeatScenario() {
		return progression < maximalLoopCount;
	}
	
	@Override
	public AbstractUserAction startScenario() {
		final AbstractUserAction userAction = super.startScenario();
		this.progression++;
		return userAction;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder extends BaseBuilder<LoopScenarioBehaviorContext, Builder> {
		
		private int maximalLoopCount;
		private int initialLoopCount;
		
		private Builder() {
			this.maximalLoopCount = -1;
			this.initialLoopCount = 0;
		}
		
		public Builder withMaximalLoopCount(final int maximalLoopCount) {
			this.maximalLoopCount = maximalLoopCount;
			return this;
		}
		
		public Builder withInitialLoopCount(final int initialLoopCount) {
			this.initialLoopCount = initialLoopCount;
			return this;
		}
		
		@Override
		public LoopScenarioBehaviorContext build() {
			return new LoopScenarioBehaviorContext(this);
		}
		
	}
}
