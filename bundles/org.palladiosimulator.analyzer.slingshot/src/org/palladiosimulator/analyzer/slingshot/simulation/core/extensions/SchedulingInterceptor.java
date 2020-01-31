package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;


public class SchedulingInterceptor extends AbstractInterceptor {

	
	private final SimulationScheduling scheduling;
	
	private final Logger LOGGER = Logger.getLogger(SchedulingInterceptor.class);

	
	public SchedulingInterceptor(SimulationScheduling scheduling) {
		this.scheduling = scheduling;
	}
	
	@Override
	public void postIntercept(final Object extension, final Method m, final Object[] args, final Object result){
		ResultEvent<DESEvent> eventResult = (ResultEvent<DESEvent>) result;
	
		for (DESEvent desEvent : eventResult.getEventsForScheduling()) {
			LOGGER.info(EventPrettyLogPrinter.prettyPrint(desEvent, "Forwarding Event", "Scheduling Interceptor"));
			scheduling.scheduleForSimulation(desEvent);
		}
		
	
	}
}
