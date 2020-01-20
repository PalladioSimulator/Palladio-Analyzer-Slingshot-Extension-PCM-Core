package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.SimulationScheduling;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ManyEvents;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.OptionalEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.SingleEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public class SchedulingInterceptor extends AbstractInterceptor {

	
	private final SimulationScheduling scheduling;
	
	private final Logger LOGGER = Logger.getLogger(SchedulingInterceptor.class);

	
	public SchedulingInterceptor(SimulationScheduling scheduling) {
		this.scheduling = scheduling;
	}
	
	@Override
	public void postIntercept(final Object extension, final Method m, final Object[] args, final Object result){
		// TODO Visitor-based implementation 
		// TODO Visiting classes that were not meant to be visited
		if(result instanceof SingleEvent) {
			
			scheduling.scheduleForSimulation(SingleEvent.class.cast(result).getEvent());
			
		} else if(result instanceof OptionalEvent) {
			
			Optional<DESEvent> optionalEvent = OptionalEvent.class.cast(result).getOptionalEvent();
			if (optionalEvent.isPresent()) {
				scheduling.scheduleForSimulation(optionalEvent.get());
			}
			
		} else if(result instanceof ManyEvents) {
			
			Set<DESEvent> events = ManyEvents.class.cast(result).getManyEvents();
			for (DESEvent desEvent : events) {
				LOGGER.info("SCHEDULING INTERCEPTOR: schedule the evt");
				scheduling.scheduleForSimulation(desEvent);
			}
		}

	}
}
