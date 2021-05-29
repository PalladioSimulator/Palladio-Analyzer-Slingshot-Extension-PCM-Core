package org.palladiosimulator.analyzer.slingshot.monitor.probe;

import javax.measure.Measure;
import javax.measure.quantity.Quantity;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.measurementframework.BasicMeasurement;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.measurement.ProbeMeasurement;
import org.palladiosimulator.probeframework.probes.Probe;

/**
 * A DESEventProbe is a probe that can listen to a specific {@link DESEvent}. As
 * soon as that DESEvent is published, a probe will take a measurement.
 * <p>
 * In order to accomplish this, the explicit, concrete type of the DESEvent is
 * given (i.e., {@code UserStarted.class}) and the monitor will fire up the
 * specific DESEventProbe when the following are true:
 * <ul>
 * <li>The specified type matches with the published event's type
 * <li>The published event is in some way related or compatible with this probe,
 * i.e., the {@code UserStarted} and {@code UserFinished} events are related if
 * their respective {@code User} entities are equal.
 * </ul>
 * 
 * @author Julijan Katic
 *
 */
public abstract class DESEventProbe<E extends DESEvent, V, Q extends Quantity> extends Probe {

	/** The event to be listened by this probe. */
	private final Class<E> eventType;

	/** The mapping of the event to the appropriate request context. */
	private final EventToRequestContextMapper requestContextMapper;

	/**
	 * Creates a new DESEventProbe listening to the given event.
	 * 
	 * @param eventType        the type of the event which this probe has to listen.
	 * @param metricDesciption the metric description as required by the superclass.
	 */
	protected DESEventProbe(final Class<E> eventType, final MetricDescription metricDesciption) {
		this(eventType, EventToRequestContextMapper.DEFAULT_MAPPER, metricDesciption);
	}

	/**
	 * Creates a new DESEventProbe listening to the given event. It also has the
	 * ability to map the event to a given request context for probe grouping as
	 * soon as the respective event has been published.
	 * 
	 * @param eventType            the type of the event which this probe has to
	 *                             listen.
	 * @param requestContextMapper the mapping for the grouping of probes.
	 * @param metricDescription    the metric description as required by the
	 *                             superclass.
	 */
	protected DESEventProbe(final Class<E> eventType,
			final EventToRequestContextMapper requestContextMapper,
			final MetricDescription metricDescription) {
		super(metricDescription);
		this.eventType = eventType;
		this.requestContextMapper = requestContextMapper;
	}

	public Class<E> getEventType() {
		return this.eventType;
	}

	public void takeMeasurement(final DESEvent event) {
		if (!event.getClass().equals(this.eventType)) {
			throw new IllegalArgumentException("This probe <" + this.getClass().getName()
					+ "> cannot be used by the given event <" + this.eventType.getName() + ">");
		}

		final BasicMeasurement<V, Q> resultMeasurement = new BasicMeasurement<>(
				this.getBasicMeasure(this.eventType.cast(event)),
				(BaseMetricDescription) this.getMetricDesciption());
		final ProbeMeasurement probeMeasurement = new ProbeMeasurement(resultMeasurement, this,
				this.requestContextMapper.mapFrom(event));
		this.notifyMeasurementSourceListener(probeMeasurement);
	}

	protected abstract Measure<V, Q> getBasicMeasure(final E event);
}
