package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data;

import org.palladiosimulator.analyzer.slingshot.monitor.data.MeasurementMade;

@FunctionalInterface
public interface ScalingTriggerPredicate {

	public boolean isTriggering(final MeasurementMade measurementMade);
	
	public static final ScalingTriggerPredicate ALWAYS = measurementMade -> true;
	public static final ScalingTriggerPredicate NEVER = measurementMade -> false;
}
