package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data;

import spd.AdjustmentType;
import spd.ScalingTrigger;
import spd.targetgroup.TargetGroup;

public final class TriggerContext {

	private final TargetGroup targetGroup;
	private final ScalingTrigger scalingTrigger;
	private final AdjustmentType adjustmentType;
	private final AdjustmentExecutor executor;

	private TriggerContext(final Builder builder) {
		this.targetGroup = builder.targetGroup;
		this.scalingTrigger = builder.scalingTrigger;
		this.adjustmentType = builder.adjustmentType;
		this.executor = builder.executor;
	}

	public void executeTrigger() {
		if (this.executor != null && this.targetGroup != null) {
			this.executor.onTrigger(this.targetGroup);
		}
	}

	/**
	 * @return the targetGroup
	 */
	public final TargetGroup getTargetGroup() {
		return this.targetGroup;
	}

	/**
	 * @return the scalingTrigger
	 */
	public final ScalingTrigger getScalingTrigger() {
		return this.scalingTrigger;
	}

	/**
	 * @return the adjustmentType
	 */
	public final AdjustmentType getAdjustmentType() {
		return this.adjustmentType;
	}

	public final AdjustmentExecutor getAdjustmentExecutor() {
		return this.executor;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private TargetGroup targetGroup;
		private ScalingTrigger scalingTrigger;
		private AdjustmentType adjustmentType;
		private AdjustmentExecutor executor;

		private Builder() {
		}

		public Builder withTargetGroup(final TargetGroup targetGroup) {
			this.targetGroup = targetGroup;
			return this;
		}

		public Builder withScalingTrigger(final ScalingTrigger scalingTrigger) {
			this.scalingTrigger = scalingTrigger;
			return this;
		}

		public Builder withAdjustmentType(final AdjustmentType adjustmentType) {
			this.adjustmentType = adjustmentType;
			return this;
		}

		public Builder withAdjustmentExecutor(final AdjustmentExecutor executor) {
			this.executor = executor;
			return this;
		}

		public TriggerContext build() {
			return new TriggerContext(this);
		}
	}
}
