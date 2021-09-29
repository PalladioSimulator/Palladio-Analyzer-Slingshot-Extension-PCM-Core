package org.palladiosimulator.analyzer.slingshot.monitor.probes;

import java.util.function.Function;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.probeframework.measurement.RequestContext;

/**
 * The event distinguisher can distinguish between two events of the same type.
 * This is done by establishing a {@link RequestContext}.
 * 
 * @author Julijan Katic
 *
 * @param <E>
 */
@FunctionalInterface
public interface EventDistinguisher<E extends DESEvent> extends Function<E, RequestContext> {

	@SuppressWarnings("rawtypes")
	public static final EventDistinguisher DEFAULT_DISTINGUISHER = event -> RequestContext.EMPTY_REQUEST_CONTEXT;

}
