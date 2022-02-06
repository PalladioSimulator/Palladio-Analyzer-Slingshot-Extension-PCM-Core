package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import java.util.LinkedList;
import java.util.List;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.measurementframework.measureprovider.IMeasureProvider;
import org.palladiosimulator.measurementframework.measureprovider.MeasurementListMeasureProvider;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.measurement.ProbeMeasurement;

public class EventBasedProbeList<V, Q extends Quantity> extends EventBasedProbe<V, Q> {

	private final List<EventBasedProbe<?, ?>> probes;

	public EventBasedProbeList(final MetricDescription metricDesciption, final List<EventBasedProbe<?, ?>> probes) {
		this(metricDesciption, EventDistinguisher.DEFAULT_DISTINGUISHER, probes);
	}

	public EventBasedProbeList(final MetricDescription metricDescription,
			final EventDistinguisher<? super DESEvent> distinguisher,
			final List<EventBasedProbe<?, ?>> probes) {
		super(metricDescription, distinguisher);
		this.probes = probes;
	}

	@Override
	public Measure<V, Q> getMeasurement(final DESEvent event) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ProbeMeasurement getProbeMeasurement(final DESEvent event) {
		final List<MeasuringValue> measuringValue = new LinkedList<>();

		for (final EventBasedProbe<?, ?> probe : this.probes) {
			final IMeasureProvider subsumedMeasureProvider = probe.getProbeMeasurement(event).getMeasureProvider();

			if (!(subsumedMeasureProvider instanceof MeasuringValue)) {
				throw new IllegalArgumentException("Subsumed measure providers have to be measurements");
			}

			measuringValue.add((MeasuringValue) subsumedMeasureProvider);
		}

		final IMeasureProvider measureProvider = new MeasurementListMeasureProvider(measuringValue);
		return new ProbeMeasurement(measureProvider, this, this.getDistinguisher().apply(event));
	}
}
