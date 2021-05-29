package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import static javax.measure.unit.SI.SECOND;

import javax.measure.Measure;
import javax.measure.quantity.Duration;

import org.palladiosimulator.analyzer.slingshot.monitor.probe.EntityBasedDESEventProbe;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

/**
 * This probe will return the current simulation time from an event.
 * 
 * @author Julijan Katic
 *
 */
public class EventCurrentSimulationTimeProbe<E extends AbstractEntityChangedEvent<?>>
		extends EntityBasedDESEventProbe<E, Double, Duration> {

	public EventCurrentSimulationTimeProbe(final Class<E> eventType) {
		super(eventType, MetricDescriptionConstants.POINT_IN_TIME_METRIC);
	}

	@Override
	protected Measure<Double, Duration> getBasicMeasure(final E event) {
		return Measure.valueOf(event.time(), SECOND);
	}

}
