package org.palladiosimulator.analyzer.slingshot.monitor.probe;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.probeframework.measurement.RequestContext;

/**
 * A {@link RequestContext} distinguishes multiple probes from each other for
 * the calculator.
 * <p>
 * A calculator can listen to multiple probe instances of the same type. The
 * probe framework gives the ability of defining request contexts in order to
 * group the right probes together, and calculate the results of each group.
 * <p>
 * E.g., the calculator will always listen to probes that listen to
 * {@code UserStarted} and {@code UserFinished} events. However, the calculator
 * must distinguish the different {@code UserStarted} events by its User entity,
 * so that - for example - the User time can be calculated correctly. Otherwise,
 * as soon as a {@code UserFinished} event is published but for a different
 * user, a wrong time might be calculated as the actual user might still reside
 * in the UserScenario.
 * <p>
 * Because the probe framework has a distinct class for such grouping, a mapping
 * has to be defined from the appropriate identifier/grouper of an event to a
 * {@link RequestContext}.
 * <p>
 * Nevertheless, this mapping is not required. If a calculator does not need a
 * grouping, then the {@link RequestContext#EMPTY_REQUEST_CONTEXT} is used as a
 * default.
 * 
 * @author Julijan Katic
 *
 */
@FunctionalInterface
public interface EventToRequestContextMapper {

	/**
	 * Maps an event to an appropriate request context in order for the calculator
	 * to distinguish and group the related probes together. Two probes are related
	 * for the calculator if the request context are equal.
	 * 
	 * @param event The event to map to the RequestContext.
	 * @return The request context distinguishing the event.
	 */
	public RequestContext mapFrom(final DESEvent event);

	/**
	 * The default mapping, which always maps an event to
	 * {@link RequestContext#EMPTY_REQUEST_CONTEXT}.
	 */
	public static final EventToRequestContextMapper DEFAULT_MAPPER = event -> RequestContext.EMPTY_REQUEST_CONTEXT;
}
