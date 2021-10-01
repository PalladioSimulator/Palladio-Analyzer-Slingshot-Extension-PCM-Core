package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.measurementframework.BasicMeasurement;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.measurement.ProbeMeasurement;
import org.palladiosimulator.probeframework.probes.Probe;

public abstract class EventBasedProbe<V, Q extends Quantity> extends Probe {

	private final Class<? extends DESEvent> eventType;
	private final EventDistinguisher<? super DESEvent> distinguisher;

	protected EventBasedProbe(final Class<? extends DESEvent> eventType, final MetricDescription metricDesciption) {
		this(eventType, metricDesciption, EventDistinguisher.DEFAULT_DISTINGUISHER);
	}

	public EventBasedProbe(final Class<? extends DESEvent> eventType, final MetricDescription metricDescription,
			final EventDistinguisher<? super DESEvent> distinguisher) {
		super(metricDescription);
		this.eventType = eventType;
		this.distinguisher = distinguisher;
	}

	public Class<? extends DESEvent> getEventType() {
		return this.eventType;
	}

	public void takeMeasurement(final DESEvent event) {
		if (!event.getClass().equals(this.eventType)) {
			throw new IllegalArgumentException("This probe <" + this.getClass().getName()
					+ "> cannot be used by the given event <" + this.eventType.getName() + ">");
		}

		final BasicMeasurement<V, Q> resultMeasurement = new BasicMeasurement<>(
				this.getMeasurement(event), (BaseMetricDescription) this.getMetricDesciption());
		final ProbeMeasurement probeMeasurement = new ProbeMeasurement(resultMeasurement, this,
				this.distinguisher.apply(event));
		this.notifyMeasurementSourceListener(probeMeasurement);
	}

	public abstract Measure<V, Q> getMeasurement(final DESEvent event);

}
