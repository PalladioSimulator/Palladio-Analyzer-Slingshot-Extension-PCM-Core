package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;

/**
 * Schedules the immediate events after invoking the extension method and
 * returning the {@link ResultEvent}.
 * 
 * @author Julijan Katic
 */
public class SchedulingInterceptor extends AbstractInterceptor {

	private final SimulationScheduling scheduling;

	private final Logger LOGGER = Logger.getLogger(SchedulingInterceptor.class);

	public SchedulingInterceptor(final SimulationScheduling scheduling) {
		this.scheduling = scheduling;
	}

	@Override
	public void postIntercept(final Object extension, final Method m, final Object[] args, final Object result) {
		final ResultEvent<DESEvent> eventResult = (ResultEvent<DESEvent>) result;

		for (final DESEvent desEvent : eventResult.getEventsForScheduling()) {
			LOGGER.info(EventPrettyLogPrinter.prettyPrint(desEvent, "Forwarding Event", "Scheduling Interceptor"));
			scheduling.scheduleForSimulation(desEvent);
		}

	}
}
