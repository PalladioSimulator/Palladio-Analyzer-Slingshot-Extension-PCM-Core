package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;

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

		ResultEvent<DESEvent> resultEvent = (ResultEvent<DESEvent>) result;
		
		for (OnEvent onEvent : onEvents) {
			if (onEvent.eventType().equals(eventClass)) {
				
				annotationExists = true;
				

				ContractResult contractResultForType = eventContractChecker.checkEventType(resultEvent, onEvent);
				
				if(contractResultForType.isFailed()) {
					LOGGER.info(EventPrettyLogPrinter.prettyPrint((DESEvent) args[0], "Failure of enforcing contract on extension reacting to the event", "Simulation Extension OnEvent Contract Enforcement Interceptor"));
					throw new RuntimeException(contractResultForType.getMessage());
				}
				
				//TODO:: MANY 
				ContractResult contractResultForCardinality = eventContractChecker.checkCardinality(resultEvent, onEvent);
				
				if(contractResultForCardinality.isFailed()) {
					LOGGER.info(EventPrettyLogPrinter.prettyPrint((DESEvent) args[0], "Enforcing contract on extension reacting to the event", "Simulation Extension OnEvent Contract Enforcement Interceptor"));
					throw new RuntimeException(contractResultForCardinality.getMessage());
				}
				
				break;

			}

		}
		
		if (!annotationExists) {
			LOGGER.info(EventPrettyLogPrinter.prettyPrint((DESEvent) args[0], "Enforcing contract on extension reacting to the event", "Simulation Extension OnEvent Contract Enforcement Interceptor"));
			throw new RuntimeException("Extension Method does not provide a contract definition");
		}
		
		LOGGER.info(EventPrettyLogPrinter.prettyPrint((DESEvent) args[0], "Enforcing contract on extension reacting to the event", "Simulation Extension OnEvent Contract Enforcement Interceptor"));


	}

}
