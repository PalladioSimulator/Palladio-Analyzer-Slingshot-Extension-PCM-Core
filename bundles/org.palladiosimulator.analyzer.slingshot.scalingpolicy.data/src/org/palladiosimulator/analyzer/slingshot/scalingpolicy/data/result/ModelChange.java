package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data.result;

import java.util.Objects;

import org.eclipse.emf.ecore.EObject;

import com.google.common.base.Preconditions;

public final class ModelChange implements Comparable<ModelChange> {
	
	private final double atSimulationTime;
	private final int oldSize;
	private final int newSize;
	private final EObject modelElement;
	private final ModelChangeAction modelChangeAction;
	
	public ModelChange(final Builder builder) {
		this.atSimulationTime = builder.atSimulationTime;
		this.oldSize = builder.oldSize;
		this.newSize = builder.newSize;
		this.modelElement = builder.modelElement;
		this.modelChangeAction = builder.modelChangeAction;
	}
	
	@Override
	public int compareTo(ModelChange o) {
		return Double.compare(atSimulationTime, o.atSimulationTime);
	}
	
	public double getAtSimulationTime() {
		return atSimulationTime;
	}

	public int getOldSize() {
		return oldSize;
	}

	public int getNewSize() {
		return newSize;
	}

	public EObject getModelElement() {
		return modelElement;
	}
	
	public ModelChangeAction getModelChangeAction() {
		return this.modelChangeAction;
	}

	@Override
	public int hashCode() {
		return Objects.hash(atSimulationTime, modelElement, modelChangeAction);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ModelChange other = (ModelChange) obj;
		return Double.doubleToLongBits(atSimulationTime) == Double.doubleToLongBits(other.atSimulationTime)
				&& Objects.equals(modelElement, other.modelElement)
				&& Objects.equals(modelChangeAction, other.modelChangeAction);
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static final class Builder {
		private double atSimulationTime;
		private int oldSize;
		private int newSize;
		private EObject modelElement;
		private ModelChangeAction modelChangeAction = ModelChangeAction.UNCHANGED;
		
		public Builder atSimulationTime(final double atSimulationTime) {
			Preconditions.checkArgument(atSimulationTime >= 0, "The simulation time must be non-negativ");
			this.atSimulationTime = atSimulationTime;
			return this;
		}
		
		public Builder withOldSize(final int oldSize) {
			this.oldSize = oldSize;
			return this;
		}
		
		public Builder withNewSize(final int newSize) {
			this.newSize = newSize;
			return this;
		}
		
		public Builder withSizes(final int oldSize, final int newSize) {
			this.withOldSize(oldSize);
			this.withNewSize(newSize);
			return this;
		}
		
		public Builder withModelElement(final EObject modelElement) {
			this.modelElement = Objects.requireNonNull(modelElement);
			return this;
		}
		
		public Builder withModelChangeAction(final ModelChangeAction modelChangeAction) {
			this.modelChangeAction = Objects.requireNonNull(modelChangeAction);
			return this;
		}
		
		public ModelChange build() {
			return new ModelChange(this);
		}
	}
}
