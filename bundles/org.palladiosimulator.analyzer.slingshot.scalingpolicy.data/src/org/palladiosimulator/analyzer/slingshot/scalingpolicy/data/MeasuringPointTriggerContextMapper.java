package org.palladiosimulator.analyzer.slingshot.scalingpolicy.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.palladiosimulator.edp2.models.measuringpoint.MeasuringPoint;

public final class MeasuringPointTriggerContextMapper {
	
	private static MeasuringPointTriggerContextMapper INSTANCE;

	private final Map<String, TriggerContext> map = new HashMap<>();
	
	public void put(final MeasuringPoint measuringPoint, final TriggerContext context) {
		Objects.requireNonNull(measuringPoint);
		Objects.requireNonNull(context);
		
		map.put(measuringPoint.getStringRepresentation(), context);
	}
	
	public ScalingTriggerPredicate wrap(final ScalingTriggerPredicate predicate) {
		return (measurementMade, context) -> {
			final MeasuringPoint point = measurementMade.getEntity().getMeasuringPoint();
			final TriggerContext mappedContext = map.get(point.getStringRepresentation());
			if (mappedContext.equals(context)) {
				return predicate.isTriggering(measurementMade, context);
			} else {
				return false;
			}
		};
	}
	
	public static MeasuringPointTriggerContextMapper instance() {
		if (INSTANCE == null) {
			INSTANCE = new MeasuringPointTriggerContextMapper();
		}
		
		return INSTANCE;
	}
}
