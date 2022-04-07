package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import spd.policyconstraint.PolicyConstraint;

public final class ConstraintResult {
	
	private final PolicyConstraint constraint;
	private final Map<BooleanSupplier, String> reasons;
	
	private ConstraintResult(final Builder builder) {
		this.constraint = builder.constraint;
		this.reasons = builder.reasons;
	}

	public PolicyConstraint getConstraint() {
		return constraint;
	}

	public static Builder builder() {
		return new Builder();
	}
	

	public static final class Builder {
		private PolicyConstraint constraint;
		private Map<BooleanSupplier, String> reasons;
		
		private Builder() {}
		
		public Builder withReason(final String reason, final BooleanSupplier predicate) {
			if (this.reasons == null) {
				this.reasons = new HashMap<>();
			}
			this.reasons.put(predicate, reason);
			return this;
		}
		
		public Builder withConstraint(final PolicyConstraint constraint) {
			this.constraint = constraint;
			return this;
		}
		
		public ConstraintResult build() {
			return new ConstraintResult(this);
		}
	}
}
