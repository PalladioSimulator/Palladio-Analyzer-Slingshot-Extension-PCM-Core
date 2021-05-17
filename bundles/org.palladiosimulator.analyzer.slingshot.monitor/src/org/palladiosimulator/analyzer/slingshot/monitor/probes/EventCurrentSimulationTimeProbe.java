package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import static javax.measure.unit.SI.SECOND;

import javax.measure.Measure;
import javax.measure.quantity.Duration;

import org.palladiosimulator.analyzer.slingshot.monitor.probe.EventProbe;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.metricspec.constants.MetricDescriptionConstants;
import org.palladiosimulator.probeframework.measurement.RequestContext;

/**
 * This probe will return the current simulation time from an event.
 * 
 * @author Julijan Katic
 *
 */
public class EventCurrentSimulationTimeProbe extends EventProbe<DESEvent, Double, Duration> {

	public EventCurrentSimulationTimeProbe(final DESEvent event) {
		super(event, MetricDescriptionConstants.POINT_IN_TIME_METRIC);
	}

	@Override
	protected Measure<Double, Duration> getBasicMeasure(final RequestContext arg0) {
		return Measure.valueOf(this.getStateObject().time(), SECOND);
	}

}
