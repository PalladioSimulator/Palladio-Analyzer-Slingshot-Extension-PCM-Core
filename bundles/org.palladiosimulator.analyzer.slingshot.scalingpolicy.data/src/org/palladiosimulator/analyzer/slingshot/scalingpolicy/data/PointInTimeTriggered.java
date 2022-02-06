package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data;

import org.palladiosimulator.analyzer.slingshot.simulation.events.ConcreteTimeEvent;

import com.google.common.base.Preconditions;

public final class PointInTimeTriggered extends AbstractTriggerEvent implements ConcreteTimeEvent {

	private final double dispatchEventAt;

	public PointInTimeTriggered(final TriggerContext context, final double specificTime) {
		super(context);
		Preconditions.checkArgument(specificTime >= 0);
		this.dispatchEventAt = specificTime;
	}

	@Override
	public double at() {
		return this.dispatchEventAt;
	}
}
