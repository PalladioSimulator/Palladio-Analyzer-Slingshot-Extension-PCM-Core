package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors;

import java.lang.reflect.Method;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.graph.DefaultEventNode;
import org.palladiosimulator.analyzer.slingshot.simulation.events.graph.EventEdge;
import org.palladiosimulator.analyzer.slingshot.simulation.events.graph.EventGraph;
import org.palladiosimulator.analyzer.slingshot.simulation.events.graph.EventNode;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

/**
 * This interceptor records all the events and their relations to each other
 * that were happening at runtime. The events are saved into an
 * {@link EventGraph} and can then be traversed there.
 * <p>
 * This interceptor already assumes that the event handling was done correctly,
 * meaning that the
 * {@link SimulationExtensionOnEventContractEnforcementInterceptor} must be done
 * first.
 * 
 * @author Julijan Katic
 */
public class EventMonitoringInterceptor extends AbstractInterceptor {

	private final EventGraph eventGraph;

	public EventMonitoringInterceptor(final EventGraph eventGraph) {
		this.eventGraph = eventGraph;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void postIntercept(final Object extension, final Method method, final Object[] args, final Object result) {
		final ResultEvent<DESEvent> resultEvent = (ResultEvent<DESEvent>) result;

		/* The parent event that caused the resulting events. */
		final DESEvent parentEvent = (DESEvent) args[0];
		final EventNode<DESEvent> parentNode = new DefaultEventNode(parentEvent);

		for (final DESEvent resultingEvent : resultEvent.getEventsForScheduling()) {
			final EventNode<DESEvent> childNode = new DefaultEventNode(resultingEvent);

			this.eventGraph.addEdge(new EventEdge(parentNode, childNode));
		}
	}

}
