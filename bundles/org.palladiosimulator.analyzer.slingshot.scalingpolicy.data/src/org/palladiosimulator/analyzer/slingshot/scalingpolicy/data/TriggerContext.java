package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;
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
	private final EObject affectedObject;

	private final ScalingTriggerPredicate scalingTriggerPredicate;
	private final List<PolicyConstraintPredicate> constraints;
	
	private Map<String, Object> parameters;
	
	private final List<AdjustmentResult> results = new LinkedList<>();

	private TriggerContext(final Builder builder) {
		this.targetGroup = builder.targetGroup;
		this.scalingTrigger = builder.scalingTrigger;
		this.adjustmentType = builder.adjustmentType;
		this.executor = builder.executor;
		this.constraints = builder.predicates;
		this.affectedObject = builder.affectedObject;
		this.scalingTriggerPredicate = builder.scalingTriggerPredicate;
	}

	/**
	 * Executes the trigger using the specified adjustment executor. This method
	 * also checks whether all the constraints are fulfilled. If not, then the the
	 * unfulfilled constraint will be returned as the result, and the result will be
	 * set to be unsuccessful.
	 * 
	 * @return
	 */
	public AdjustmentResult executeTrigger(final MeasurementMade measurementMade) {
		this.setParameters(parameters);
		
		if (measurementMade != null && !this.scalingTriggerPredicate.isTriggering(measurementMade, this)) {
			return AdjustmentResult.NO_TRIGGER;
		}
		
		for (final PolicyConstraintPredicate predicate : this.constraints) {
			final ConstraintResult result = predicate.apply(this);
			if (!result.isSuccess()) {
				return this.recordResult(AdjustmentResult.builder()
						.addConstraint(result)
						.success(AdjustmentResultReason.UNFULFILLED_CONSTRAINT)
						.build());
			}
		}
		
		if (this.executor != null && this.targetGroup != null) {
			return this.recordResult(this.executor.onTrigger(this));
		} else {
			throw new IllegalStateException("Either the executor or the target group is not given.");
		}
	}
	
	public AdjustmentResult executeTrigger() {
		return this.executeTrigger(null);
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
	
	public EObject getAffectedObject() {
		return this.affectedObject;
	}
	
	public void setParameters(final Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	public Map<String, Object> getParameters() {
		return Collections.unmodifiableMap(this.parameters);
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
	
	private AdjustmentResult recordResult(final AdjustmentResult result) {
		assert result != null;
		this.results.add(result);
		return result;
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private TargetGroup targetGroup;
		private ScalingTrigger scalingTrigger;
		private ScalingTriggerPredicate scalingTriggerPredicate;
		private AdjustmentType adjustmentType;
		private AdjustmentExecutor executor;
		private final List<PolicyConstraintPredicate> predicates = new LinkedList<>();
		private EObject affectedObject;
		private final List<Consumer<TriggerContext>> afterBuilders = new LinkedList<>();
		
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
		
		public Builder withAffectedObject(final EObject affectedObject) {
			this.affectedObject = affectedObject;
			return this;
		}
		
		public Builder withScalingTriggerPredicate(final ScalingTriggerPredicate scalingTriggerPredicate) {
			this.scalingTriggerPredicate = scalingTriggerPredicate;
			return this;
		}
		
		public Builder onBuild(final Consumer<TriggerContext> onBuild) {
			if (onBuild != null) {
				this.afterBuilders.add(onBuild);
			}
			return this;
		}

		public TriggerContext build() {
			final TriggerContext result = new TriggerContext(this);
			this.afterBuilders.forEach(onBuilder -> onBuilder.accept(result));
			return result;
		}
		
	}
}
