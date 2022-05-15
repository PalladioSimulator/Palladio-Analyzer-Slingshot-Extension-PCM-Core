package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
		
		if (builder.modifyReasons != null) {
			for (final ConstraintReason constraintReason : builder.modifyReasons) {
				if (!constraintReason.predicate.getAsBoolean()) {
					constraintReason.modifier
									.ifPresentOrElse(
											Modifier::modify, 
											() -> currentReasons.add(constraintReason.reason)
									);
				}
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
		private List<ConstraintReason> modifyReasons;
		
		private Builder() {
		}

		public Builder withReason(final String reason, final BooleanSupplier predicate) {
			this.withModifiableReason(reason, predicate, null);
			return this;
		}
		
		public Builder withModifiableReason(final String reason, final BooleanSupplier predicate, final Modifier modifier) {
			if (this.modifyReasons == null) {
				this.modifyReasons = new LinkedList<>();
			}
			this.modifyReasons.add(new ConstraintReason(reason, predicate, modifier));
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
	
	private static final class ConstraintReason {
		
		private final String reason;
		private final BooleanSupplier predicate;
		private final Optional<Modifier> modifier;
		
		private ConstraintReason(String reason, BooleanSupplier predicate, Modifier modifier) {
			super();
			this.reason = Objects.requireNonNull(reason);
			this.predicate = Objects.requireNonNull(predicate);
			this.modifier = Optional.ofNullable(modifier);
		}
		
		
	}
	
	@FunctionalInterface
	public interface Modifier {
		void modify();
	}
}
