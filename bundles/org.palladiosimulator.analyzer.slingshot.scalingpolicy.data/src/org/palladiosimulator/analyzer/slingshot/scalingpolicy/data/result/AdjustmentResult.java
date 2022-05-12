package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;
import org.palladiosimulator.monitorrepository.Monitor;
import org.palladiosimulator.pcm.allocation.AllocationContext;

/**
 * Instances of this class contains all the results and information about an
 * adjustment round.
 * 
 * @author Julijan Katic
 *
 */
public final class AdjustmentResult {

	public static final AdjustmentResult EMPTY_RESULT = new AdjustmentResult();

	private final String id;
	private final TriggerContext context;
	private final AdjustmentResultReason adjustmentResultReason;

	private final List<ModelChange> changes;
	private final List<ConstraintResult> constraintResults;
	private final List<Monitor> newMonitors;
	private final List<AllocationContext> newAllocationContexts;

	private AdjustmentResult(final Builder builder) {
		this.context = builder.context;
		this.adjustmentResultReason = builder.reason;
		this.changes = builder.changes;
		this.id = UUID.randomUUID().toString();
		this.constraintResults = builder.constraintResults;
		this.newMonitors = builder.newMonitors;
		this.newAllocationContexts = builder.newAllocationContexts;
	}

	private AdjustmentResult() {
		this.id = "";
		this.context = null;
		this.adjustmentResultReason = AdjustmentResultReason.SUCCESS;
		this.changes = Collections.emptyList();
		this.constraintResults = null;
		this.newMonitors = Collections.emptyList();
		this.newAllocationContexts = Collections.emptyList();
	}

	public TriggerContext getContext() {
		return this.context;
	}

	public boolean isSuccess() {
		return this.adjustmentResultReason == AdjustmentResultReason.SUCCESS;
	}

	public List<ModelChange> getChanges() {
		return this.changes;
	}

	public AdjustmentResultReason getReason() {
		return this.adjustmentResultReason;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final AdjustmentResult other = (AdjustmentResult) obj;
		return Objects.equals(this.id, other.id);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private TriggerContext context;
		private AdjustmentResultReason reason = AdjustmentResultReason.SUCCESS;
		private List<ModelChange> changes;
		private List<ConstraintResult> constraintResults;
		private List<Monitor> newMonitors;
		private List<AllocationContext> newAllocationContexts;

		private Builder() {
		}

		public Builder withTriggerContext(final TriggerContext context) {
			this.context = context;
			return this;
		}

		public Builder success(final AdjustmentResultReason reason) {
			this.reason = Objects.requireNonNull(reason);
			return this;
		}

		public Builder withChanges(final List<ModelChange> changes) {
			this.changes = Objects.requireNonNull(changes);
			return this;
		}

		public Builder addChange(final ModelChange change) {
			if (this.changes == null) {
				this.changes = new LinkedList<>();
			}
			this.changes.add(change);
			return this;
		}

		public Builder addConstraint(final ConstraintResult constraintResult) {
			if (this.constraintResults == null) {
				this.constraintResults = new LinkedList<>();
			}
			this.constraintResults.add(constraintResult);
			return this;
		}

		public Builder addNewMonitor(final Monitor monitor) {
			if (this.newMonitors == null) {
				this.newMonitors = new LinkedList<>();
			}
			this.newMonitors.add(monitor);
			return this;
		}

		public Builder addNewAllocationContext(final AllocationContext copiedContext) {
			if (this.newAllocationContexts == null) {
				this.newAllocationContexts = new LinkedList<>();
			}
			this.newAllocationContexts.add(copiedContext);
			return this;
		}

		public AdjustmentResult build() {
			return new AdjustmentResult(this);
		}
	}

}
