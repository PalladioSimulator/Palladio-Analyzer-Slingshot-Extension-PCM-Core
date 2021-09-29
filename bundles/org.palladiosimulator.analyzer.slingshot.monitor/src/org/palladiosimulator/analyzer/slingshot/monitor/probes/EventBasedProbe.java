package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.measurementframework.BasicMeasurement;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.measurement.ProbeMeasurement;
import org.palladiosimulator.probeframework.probes.Probe;

public abstract class EventBasedProbe<E extends DESEvent, V, Q extends Quantity> extends Probe {

	private final Class<E> eventType;
	private final EventDistinguisher<E> distinguisher;

	protected EventBasedProbe(final Class<E> eventType, final MetricDescription metricDesciption) {
		this(eventType, metricDesciption, EventDistinguisher.DEFAULT_DISTINGUISHER);
	}

	public EventBasedProbe(final Class<E> eventType, final MetricDescription metricDescription,
			final EventDistinguisher<E> distinguisher) {
		super(metricDescription);
		this.eventType = eventType;
		this.distinguisher = distinguisher;
	}

	public Class<E> getEventType() {
		return this.eventType;
	}

	public void takeMeasurement(final DESEvent event) {
		if (!event.getClass().equals(this.eventType)) {
			throw new IllegalArgumentException("This probe <" + this.getClass().getName()
					+ "> cannot be used by the given event <" + this.eventType.getName() + ">");
		}

		final E concreteEvent = this.eventType.cast(event);
		final BasicMeasurement<V, Q> resultMeasurement = new BasicMeasurement<>(
				this.getMeasurement(concreteEvent), (BaseMetricDescription) this.getMetricDesciption());
		final ProbeMeasurement probeMeasurement = new ProbeMeasurement(resultMeasurement, this,
				this.distinguisher.apply(concreteEvent));
		this.notifyMeasurementSourceListener(probeMeasurement);
	}

	public abstract Measure<V, Q> getMeasurement(final E event);

}
