package org.palladiosimulator.analyzer.slingshot.monitor.probe;

import javax.measure.quantity.Quantity;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.metricspec.MetricDescription;
import org.palladiosimulator.probeframework.measurement.RequestContext;

/**
 * The entity based DESEvent probe already maps a event that has an entity (see
 * {@link AbstractEntityChangedEvent}) to the request context. This is done by
 * using the respective {@code toString()} method of the entity that can be used
 * as a distinguisher of the probes, as the events holding the same entity will
 * have the same {@code toString()} method. (TODO: YOU MUST ENSURE THAT!)
 * 
 * @author Julijan Katic
 *
 */
public abstract class EntityBasedDESEventProbe<E extends AbstractEntityChangedEvent<?>, V, Q extends Quantity>
		extends DESEventProbe<E, V, Q> {

	/* We can be sure that the event will be of type AbstractEntityChangedEvent. */
	private static final EventToRequestContextMapper ENTITY_REQUEST_CONTEXT = event -> {
		final var entityEvent = (AbstractEntityChangedEvent<?>) event;
		return new RequestContext(entityEvent.getEntity().toString());
	};

	protected EntityBasedDESEventProbe(final Class<E> eventType,
			final MetricDescription metricDescription) {
		super(eventType, ENTITY_REQUEST_CONTEXT, metricDescription);
	}

}
