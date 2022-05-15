package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;

@FunctionalInterface
public interface ScalingTriggerPredicate {

	public boolean isTriggering(final MeasurementMade measurementMade, final TriggerContext context);
	
	public static final ScalingTriggerPredicate ALWAYS = (measurementMade, context) -> true;
	public static final ScalingTriggerPredicate NEVER = (measurementMade, context) -> false;
}
