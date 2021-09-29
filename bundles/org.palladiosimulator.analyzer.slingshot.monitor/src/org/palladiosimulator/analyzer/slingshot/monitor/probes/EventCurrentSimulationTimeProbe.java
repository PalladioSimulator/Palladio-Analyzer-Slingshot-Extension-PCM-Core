package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;

public final class EventCurrentSimulationTimeProbe<E extends DESEvent> extends EventBasedProbe<E, Double, Duration> {

	public EventCurrentSimulationTimeProbe(final Class<E> eventType) {
		super(eventType, MetricDescriptionConstants.POINT_IN_TIME_METRIC);
	}

	public EventCurrentSimulationTimeProbe(final Class<E> eventType, final EventDistinguisher<E> distinguisher) {
		super(eventType, MetricDescriptionConstants.POINT_IN_TIME_METRIC, distinguisher);
	}

	@Override
	public Measure<Double, Duration> getMeasurement(final E event) {
		return Measure.valueOf(event.time(), SI.SECOND);
	}

}
