package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;

import java.lang.reflect.Method;


import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ManyEvents;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.OptionalEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.SingleEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public class ContractEnforcementInterceptor implements Interceptor {

	private final Logger LOGGER = Logger.getLogger(ContractEnforcementInterceptor.class);
	
	
	public ContractEnforcementInterceptor() {
	}
	
	@Override
	public void preIntercept(final Object extension, final Method m, final Object[] args) {
	
	}
	
	@Override
	public void postIntercept(final Object result, final Object self, final Method m, final Object[] args) {

		if(m.getName().equals("init"))
		{
			return;
		}
		
		if(args.length!=1) {
			//TODO:: Do we need to have our own custom Exceptions for extensions
			new IllegalArgumentException("Extension Method is allowed to react only on one and exactly one event");
		}
		
		if (self.getClass().getSuperclass().isAnnotationPresent(OnEvent.OnEvents.class)) {
			
			OnEvent.OnEvents anno = self.getClass().getSuperclass().getAnnotation(OnEvent.OnEvents.class);
			OnEvent[] onEvents = anno.value();
			
			Class eventClass = args[0].getClass();
			boolean annotationExists = false;
	
			for (OnEvent onEvent : onEvents ) {
				if(onEvent.eventType().equals(eventClass)) {
					annotationExists = true;
					LOGGER.info("Annotation for the event type: " +onEvent.eventType().getName() +" exists");
					Class<? extends DESEvent> outputType = onEvent.outputEventType();

					if(!result.getClass().equals(outputType)) {
						
					}
					
					if(result instanceof ManyEvents) {
						if(!onEvent.cardinality().equals(EventCardinality.MANY)) {
							throw new RuntimeException("Extension Method Is Not Returning the Cardinality According to Contract, Returned: Many Events, Contract: "+ onEvent.cardinality().toString());
						}
						
					} else if (result instanceof SingleEvent) {
						if(!onEvent.cardinality().equals(EventCardinality.SINGLE)) {
							throw new RuntimeException("Extension Method Is Not Returning the Cardinality According to Contract, Returned: Single, Contract: "+ onEvent.cardinality().toString());
						}
						
					} else if (result instanceof OptionalEvent) {
						if(!onEvent.cardinality().equals(EventCardinality.SINGLE)) {
							throw new RuntimeException("Extension Method Is Not Returning the Cardinality According to Contract, Returned: OptionalEvent with Cardinality Single, Contract: "+ onEvent.cardinality().toString());
						}
					}
					return;
					
				}
			}
			
			if(!annotationExists) {
				throw new RuntimeException("Extension Method does not provide a contract definition");
			}
			
		}

	}

}
