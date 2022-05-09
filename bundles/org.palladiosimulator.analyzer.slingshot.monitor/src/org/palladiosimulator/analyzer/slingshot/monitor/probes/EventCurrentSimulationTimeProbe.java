package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import java.util.function.Function;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.ModelPassedEvent;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.probeframework.measurement.ProbeMeasurement;
import org.palladiosimulator.probeframework.measurement.RequestContext;

/**
 * A standard implementation of a {@link DESEvent}-based probe that takes the
 * current simulation time. The MetricDescription is
 * {@link MetricDescriptionConstants#POINT_IN_TIME_METRIC}.
 * 
 * @author Julijan Katic
 */
public final class EventCurrentSimulationTimeProbe extends EventBasedProbe<Double, Duration> {

	/**
	 * Constructs a EventCurrentSimulationTimeProbe.
	 * 
	 * @param eventType The type of the event.
	 */
	public EventCurrentSimulationTimeProbe() {
		super(MetricDescriptionConstants.POINT_IN_TIME_METRIC);
	}

	/**
	 * Constructs an EventCurrentSimulationTimeProbe with a custom distinguisher.
	 * 
	 * @param eventType     The type of the event.
	 * @param distinguisher The distinguisher that instantiates a
	 *                      {@link RequestContext}.
	 */
	public EventCurrentSimulationTimeProbe(
			final EventDistinguisher distinguisher) {
		super(MetricDescriptionConstants.POINT_IN_TIME_METRIC, distinguisher);
	}

	@Override
	public Measure<Double, Duration> getMeasurement(final DESEvent event) {
		return Measure.valueOf(event.time(), SI.SECOND);
	}

	/**
	 * Convenience method to instantiate a EventCurrentSimulationTimeProbe from a
	 * {@link ModelPassedEvent} with an appropriate distinguisher.
	 * 
	 * @param <E>       The model element type.
	 * @param <F>       The exact event type.
	 * @param eventType The event type.
	 * @param transform A function that transforms the event into a unique string
	 *                  for the request context.
	 * @return A new EventCurrentSimulationTimeProbe from a ModelPassedEvent.
	 */
	public static <E extends EObject, F extends ModelPassedEvent<E>> EventCurrentSimulationTimeProbe modelPassedEventProbe(
			final Function<F, String> transform) {
		return new EventCurrentSimulationTimeProbe(
				event -> new RequestContext(transform.apply((F) event)));
	}

}
