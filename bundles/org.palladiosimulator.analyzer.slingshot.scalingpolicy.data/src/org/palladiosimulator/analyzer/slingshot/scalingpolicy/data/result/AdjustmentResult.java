package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.TriggerContext;

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
	private final boolean success;
	private final List<ModelChange> changes;
	
	private AdjustmentResult(final Builder builder) {
		this.context = builder.context;
		this.success = builder.success;
		this.changes = builder.changes;
		this.id = UUID.randomUUID().toString();
	}
	
	private AdjustmentResult() {
		this.id = "";
		this.context = null;
		this.success = true;
		this.changes = Collections.emptyList();
	}
	
	public TriggerContext getContext() {
		return context;
	}

	public boolean isSuccess() {
		return success;
	}

	public List<ModelChange> getChanges() {
		return changes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AdjustmentResult other = (AdjustmentResult) obj;
		return Objects.equals(id, other.id);
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		private TriggerContext context;
		private boolean success;
		private List<ModelChange> changes; 
		
		private Builder() {}
		
		public Builder withTriggerContext(final TriggerContext context) {
			this.context = context;
			return this;
		}
		
		public Builder success(final boolean success) {
			this.success = success;
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
		
		public AdjustmentResult build() {
			return new AdjustmentResult(this);
		}
	}
	
}
