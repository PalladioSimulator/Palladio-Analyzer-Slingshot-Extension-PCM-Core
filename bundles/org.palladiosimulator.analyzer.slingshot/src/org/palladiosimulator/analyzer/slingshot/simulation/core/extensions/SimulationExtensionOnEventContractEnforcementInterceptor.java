package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.events.SimulationStarted;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ManyEvents;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.OptionalEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.SingleEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events.UserStarted;

public class SimulationExtensionOnEventContractEnforcementInterceptor extends AbstractInterceptor {

	private final Logger LOGGER = Logger.getLogger(SimulationExtensionOnEventContractEnforcementInterceptor.class);

	private EventContractChecker eventContractChecker;
	
	public SimulationExtensionOnEventContractEnforcementInterceptor() {
		this.eventContractChecker = new EventContractChecker();
	}

	@Override
	public void postIntercept(final Object extension, final Method m, final Object[] args, final Object result) {

		// 0. Annotations are present at all
		// 1. contracts are defined
		// 2. the cardinality of the returned result matches the contract definition
		
		if (!extension.getClass().getSuperclass().isAnnotationPresent(OnEvent.OnEvents.class)) {
			throw new RuntimeException("Extension does not provide any contract definition");
		}

		OnEvent.OnEvents annotations = extension.getClass().getSuperclass().getAnnotation(OnEvent.OnEvents.class);
		OnEvent[] onEvents = annotations.value();

		Class eventClass = args[0].getClass(); // e.g., SimulationStarted -> ManyEvent<UserStarted>
		boolean annotationExists = false;

		for (OnEvent onEvent : onEvents) {
			if (onEvent.eventType().equals(eventClass)) {
				
				annotationExists = true;
				LOGGER.info("Annotation for the event type: " + onEvent.eventType().getName() + " exists");
				
				//TODO:: ManyEvents<UserStarted> is really having UserStarted as the outputEventType specifies is problematic
//				// 
//				ContractResult contractResultForType = eventContractChecker.checkEventType(result, onEvent);
//				
//				if(contractResultForType.isFailed()) {
//					throw new RuntimeException(contractResultForType.getMessage());
//				}
				
				ContractResult contractResultForCardinality = eventContractChecker.checkCardinality(result, onEvent);
				
				if(contractResultForCardinality.isFailed()) {
					throw new RuntimeException(contractResultForCardinality.getMessage());
				}
				
				return;

			}

		}
		
		if (!annotationExists) {
			throw new RuntimeException("Extension Method does not provide a contract definition");
		}


	}

}
