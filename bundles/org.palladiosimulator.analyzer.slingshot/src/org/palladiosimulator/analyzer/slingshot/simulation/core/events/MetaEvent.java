package org.palladiosimulator.analyzer.slingshot.simulation.core.events;

import java.util.Objects;
import java.util.Optional;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

import com.google.common.base.Preconditions;

public final class MetaEvent extends AbstractEvent {

	private final DESEvent event;
	private final Optional<Double> postEventAtTime;

	private MetaEvent(final Builder builder) {
		this.event = Objects.requireNonNull(builder.event);
		this.postEventAtTime = Optional.ofNullable(builder.postEventAtTime);
		this.check();
	}

	/**
	 * @return the event
	 */
	public final DESEvent getEvent() {
		return this.event;
	}

	/**
	 * @return the postEventAtTime
	 */
	public final Optional<Double> getPostEventAtTime() {
		return this.postEventAtTime;
	}

	private void check() {
		this.postEventAtTime.ifPresent(time -> Preconditions.checkArgument(time >= 0, "time must be positive"));
	}

	public static MetaEvent createFromSpecificPointInTime(final DESEvent event, final double pointInTime) {
		return new Builder()
				.withEvent(event)
				.withPointInTime(pointInTime)
				.build();
	}

	private static final class Builder {
		private DESEvent event;
		private Double postEventAtTime;

		public Builder withEvent(final DESEvent event) {
			this.event = event;
			return this;
		}

		public Builder withPointInTime(final Double time) {
			this.postEventAtTime = time;
			return this;
		}

		public MetaEvent build() {
			return new MetaEvent(this);
		}
	}
}
