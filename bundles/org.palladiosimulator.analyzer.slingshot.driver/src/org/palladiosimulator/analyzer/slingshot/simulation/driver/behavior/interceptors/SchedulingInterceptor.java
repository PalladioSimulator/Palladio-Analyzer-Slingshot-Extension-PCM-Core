package org.palladiosimulator.analyzer.slingshot.simulation.driver.behavior.interceptors;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.api.SimulationScheduling;
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

	private static final Logger LOGGER = Logger.getLogger(SchedulingInterceptor.class);

	private final SimulationScheduling scheduling;

	public SchedulingInterceptor(final SimulationScheduling scheduler) {
		this.scheduling = scheduler;
	}

	@Override
	public void postIntercept(final Object extension, final Method m, final Object[] args, final Object result) {
		if (!(result instanceof ResultEvent<?>)) {
			throw new IllegalArgumentException("The return type of the intercepted method is not ResultEvent!");
		}

		final ResultEvent<?> eventResult = (ResultEvent<?>) result;

		for (final DESEvent desEvent : eventResult.getEventsForScheduling()) {
			LOGGER.info(EventPrettyLogPrinter.prettyPrint(desEvent, "Forwarding Event", "Scheduling Interceptor"));
			this.scheduling.scheduleForSimulation(desEvent);
		}

	}
}
