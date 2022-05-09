package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

import com.google.common.collect.ImmutableList;

import spd.policyconstraint.PolicyConstraint;

public final class ConstraintResult {
	
	private final PolicyConstraint constraint;

	private final boolean success;
	private final List<String> reasons;

	private ConstraintResult(final Builder builder) {
		this.constraint = builder.constraint;

		boolean currentResult = true;
		final List<String> currentReasons = new LinkedList<>();
		for (final Map.Entry<BooleanSupplier, String> reasonSupplier : builder.reasons.entrySet()) {
			final boolean supplierResult = reasonSupplier.getKey().getAsBoolean();
			if (!supplierResult) {
				currentResult = false;
				currentReasons.add(reasonSupplier.getValue());
			}
		}
		this.success = currentResult;
		this.reasons = ImmutableList.copyOf(currentReasons);
	}

	public PolicyConstraint getConstraint() {
		return this.constraint;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public List<String> reasons() {
		return this.reasons;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(final PolicyConstraint constraint) {
		return builder().withConstraint(constraint);
	}

	public static final class Builder {
		private PolicyConstraint constraint;
		private Map<BooleanSupplier, String> reasons;

		private Builder() {
		}

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
