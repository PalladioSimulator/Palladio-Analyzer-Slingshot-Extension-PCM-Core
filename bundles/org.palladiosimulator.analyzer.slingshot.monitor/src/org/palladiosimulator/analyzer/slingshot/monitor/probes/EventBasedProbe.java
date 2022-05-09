package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.edp2.models.ExperimentData.Measurement;
import org.palladiosimulator.measurementframework.BasicMeasurement;
import org.palladiosimulator.measurementframework.MeasuringValue;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.measurement.ProbeMeasurement;
import org.palladiosimulator.probeframework.measurement.RequestContext;
import org.palladiosimulator.probeframework.probes.Probe;

/**
 * A probe that is {@link DESEvent}-based. Measurements/Probes are taken when
 * the associated {@link DESEvent} was published.
 * 
 * @author Julijan Katic
 *
 * @param <V> The value type of the measurement.
 * @param <Q> The quantity type of the measurement.
 */
public abstract class EventBasedProbe<V, Q extends Quantity> extends Probe {

	/** The distinguisher that is used for creating {@link RequestContext}s. */
	private final EventDistinguisher distinguisher;

	/**
	 * Constructs an event-based probe with
	 * {@link EventDistinguisher#DEFAULT_DISTINGUISHER}.
	 * 
	 * @param eventType        The event type to listen to.
	 * @param metricDesciption A metric description needed by the super-class.
	 */
	protected EventBasedProbe(final MetricDescription metricDesciption) {
		this(metricDesciption, EventDistinguisher.DEFAULT_DISTINGUISHER);
	}

	/**
	 * Constructs an event-based probe.
	 * 
	 * @param eventType        The event type to listen to.
	 * @param metricDesciption A metric description needed by the super-class.
	 * @param distinguisher    The distinguisher that is used for creating
	 *                         {@link RequestContext}s.
	 */
	public EventBasedProbe(final MetricDescription metricDescription,
			final EventDistinguisher distinguisher) {
		super(metricDescription);
		this.distinguisher = distinguisher;
	}

	/**
	 * Takes a measurement of the event type. The passed event must be of the given
	 * type by {@link #getEventType()}.
	 * <p>
	 * This calls {@link #getMeasure(DESEvent)} and wraps it into a
	 * {@link ProbeMeasurement} and notifies the listeners.
	 * 
	 * @param event The event that happened and is of type {@link #getEventType()}.
	 * @throws IllegalArgumentException if the passed event is not of the type
	 *                                  returned by {@link #getEventType()}.
	 * @see #getMeasurement(DESEvent)
	 */
	public void takeMeasurement(final DESEvent event) {
		final ProbeMeasurement probeMeasurement = this.getProbeMeasurement(event);
		this.notifyMeasurementSourceListener(probeMeasurement);
	}

	/**
	 * An implementation that returns a {@link Measure} from a {@link DESEvent}.
	 * That measure is needed by {@link #takeMeasurement(DESEvent)}.
	 * 
	 * @param event The event that happened.
	 * @return A {@link Measure} from the event.
	 */
	public abstract Measure<V, Q> getMeasurement(final DESEvent event);

	protected ProbeMeasurement getProbeMeasurement(final DESEvent event) {
		final BasicMeasurement<V, Q> resultMeasurement = new BasicMeasurement<>(
				this.getMeasurement(event), (BaseMetricDescription) this.getMetricDesciption());
		return new ProbeMeasurement(resultMeasurement, this, this.getDistinguisher().apply(event));
	}

	public EventDistinguisher getDistinguisher() {
		return this.distinguisher;
	}
}
