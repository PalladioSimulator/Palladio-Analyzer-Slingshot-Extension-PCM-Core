package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import java.util.function.Function;

import javax.measure.Measure;
import javax.measure.quantity.Duration;
import javax.measure.unit.SI;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.ModelPassedEvent;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.probeframework.measurement.RequestContext;

public final class EventCurrentSimulationTimeProbe extends EventBasedProbe<Double, Duration> {

	public EventCurrentSimulationTimeProbe(final Class<? extends DESEvent> eventType) {
		super(eventType, MetricDescriptionConstants.POINT_IN_TIME_METRIC);
	}

	public EventCurrentSimulationTimeProbe(final Class<? extends DESEvent> eventType,
			final EventDistinguisher<? super DESEvent> distinguisher) {
		super(eventType, MetricDescriptionConstants.POINT_IN_TIME_METRIC, distinguisher);
	}

	@Override
	public Measure<Double, Duration> getMeasurement(final DESEvent event) {
		return Measure.valueOf(event.time(), SI.SECOND);
	}

	public static <E extends EObject, F extends ModelPassedEvent<E>> EventCurrentSimulationTimeProbe modelPassedEventProbe(
			final Class<F> eventType, final Function<F, String> transform) {
		return new EventCurrentSimulationTimeProbe(eventType,
				event -> new RequestContext(transform.apply((F) event)));
	}
}
