package org.palladiosimulator.analyzer.slingshot.monitor.probe;

import javax.measure.quantity.Quantity;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.metricspec.BaseMetricDescription;
import org.palladiosimulator.probeframework.probes.BasicObjectStateProbe;

/**
 * A probe that is triggered as soon as a {@link DESEvent} was published.
 * 
 * @author Julijan Katic
 */
public abstract class EventProbe<E extends DESEvent, V, Q extends Quantity> extends BasicObjectStateProbe<E, V, Q> {

	public EventProbe(final E event, final BaseMetricDescription metricSpec) {
		super(event, metricSpec);
	}

}
