package org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities;

/**
 * This class is a context for OpenWorkload users.
 * 
 * @author julijankatic
 *
 */
public final class OpenWorkloadUserInterpretationContext extends UserInterpretationContext {
	
	/** The interArrivalTime to use. */
	private final InterArrivalTime interArrivalTime;

	public OpenWorkloadUserInterpretationContext(final Builder builder) {
		super(builder);
		this.interArrivalTime = builder.getInterArrivalTime();
	}
	
	public InterArrivalTime getInterArrivalTime() {
		return this.interArrivalTime;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Builder update() {
		return (Builder) this.updateWithBuilder(builder());
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder extends BaseBuilder<OpenWorkloadUserInterpretationContext, Builder> {
		
		private InterArrivalTime interArrivalTime;
		
		public Builder withInterArrivalTime(final InterArrivalTime interArrivalTime) {
			this.interArrivalTime = interArrivalTime;
			return this;
		}
		
		public InterArrivalTime getInterArrivalTime() {
			return this.interArrivalTime;
		}
		
		@Override
		public OpenWorkloadUserInterpretationContext build() {
			return new OpenWorkloadUserInterpretationContext(this);
		}

	}

}
