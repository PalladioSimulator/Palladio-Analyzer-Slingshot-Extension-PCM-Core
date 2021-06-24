package org.palladiosimulator.analyzer.slingshot.monitor.data;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.measurementframework.MeasuringValue;

/**
 * The event describes that a new {@link MeasuringValue} is available. This
 * event can be used to self-adapt models.
 * 
 * @author Julijan Katic
 */
public final class MeasurementMade extends AbstractEntityChangedEvent<MeasuringValue> {

	public MeasurementMade(final MeasuringValue entity) {
		super(entity, 0);
	}

}
