package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data;

import java.util.LinkedList;
import java.util.List;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResult;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.AdjustmentResultReason;
import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result.ConstraintResult;

import spd.adjustmenttype.AdjustmentType;
import spd.scalingtrigger.ScalingTrigger;
import spd.targetgroup.TargetGroup;

public final class TriggerContext {

	private final TargetGroup targetGroup;
	private final ScalingTrigger scalingTrigger;
	private final AdjustmentType adjustmentType;
	private final AdjustmentExecutor executor;

	private final List<PolicyConstraintPredicate> constraints;

	private TriggerContext(final Builder builder) {
		this.targetGroup = builder.targetGroup;
		this.scalingTrigger = builder.scalingTrigger;
		this.adjustmentType = builder.adjustmentType;
		this.executor = builder.executor;
		this.constraints = builder.predicates;
	}

	/**
	 * Executes the trigger using the specified adjustment executor. This method
	 * also checks whether all the constraints are fulfilled. If not, then the the
	 * unfulfilled constraint will be returned as the result, and the result will be
	 * set to be unsuccessful.
	 * 
	 * @return
	 */
	public AdjustmentResult executeTrigger() {
		for (final PolicyConstraintPredicate predicate : this.constraints) {
			final ConstraintResult result = predicate.apply(this);
			if (!result.isSuccess()) {
				return AdjustmentResult.builder()
						.addConstraint(result)
						.success(AdjustmentResultReason.UNFULFILLED_CONSTRAINT)
						.build();
			}
		}

		if (this.executor != null && this.targetGroup != null) {
			return this.executor.onTrigger(this);
		} else {
			throw new IllegalStateException("Either the executor or the target group is not given.");
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

	/**
	 * Returns the specified executor of this trigger context. DO NOT use this to
	 * start the adjustment. Use {@link #executeTrigger()} instead, as that method
	 * also checks all constraints.
	 * 
	 * @return The specified adjustment executor for this function.
	 */
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
		private final List<PolicyConstraintPredicate> predicates = new LinkedList<>();

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

		public Builder withConstraints(final List<PolicyConstraintPredicate> constraints) {
			this.predicates.addAll(constraints);
			return this;
		}

		public Builder withConstraint(final PolicyConstraintPredicate predicate) {
			this.predicates.add(predicate);
			return this;
		}

		public TriggerContext build() {
			return new TriggerContext(this);
		}
	}
}
