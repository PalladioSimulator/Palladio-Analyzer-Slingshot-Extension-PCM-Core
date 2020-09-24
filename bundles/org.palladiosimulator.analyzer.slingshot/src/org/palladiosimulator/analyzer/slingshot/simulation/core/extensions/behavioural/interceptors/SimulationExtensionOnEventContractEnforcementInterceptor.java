package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.interceptors;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioural.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;
import org.palladiosimulator.analyzer.slingshot.simulation.interceptor.AbstractInterceptor;

/**
 * This class post-intercepts the extension methods in order to enforce the specified contract.
 * 
 * @author Julijan Katic
 */
public class SimulationExtensionOnEventContractEnforcementInterceptor extends AbstractInterceptor {

	private final Logger LOGGER = Logger.getLogger(SimulationExtensionOnEventContractEnforcementInterceptor.class);
	
	public SimulationExtensionOnEventContractEnforcementInterceptor() {
	}

	@Override
	public void postIntercept(final Object extension, final Method m, final Object[] args, final Object result) {

		// 0. Annotations are present at all
		// 1. contracts are defined
		// 2. the cardinality of the returned result matches the contract definition
		
		if (!extension.getClass().getSuperclass().isAnnotationPresent(OnEvent.OnEvents.class)) {
			throw new RuntimeException("Extension does not provide any contract definition");
		}

		final OnEvent.OnEvents annotations = extension.getClass().getSuperclass().getAnnotation(OnEvent.OnEvents.class);
		final OnEvent[] onEvents = annotations.value();

		final Class<?> eventClass = args[0].getClass(); // e.g., SimulationStarted -> ManyEvent<UserStarted>
		boolean annotationExists = false;

		final ResultEvent<DESEvent> resultEvent = (ResultEvent<DESEvent>) result;
		
		for (final OnEvent onEvent : onEvents) {
			if (onEvent.when().equals(eventClass)) {
				
				annotationExists = true;
				

				final ContractResult contractResultForType = EventContractChecker.checkEventType(resultEvent, onEvent);
				
				if(contractResultForType.isFailed()) {
					LOGGER.info(EventPrettyLogPrinter.prettyPrint((DESEvent) args[0], "Failure of enforcing contract on extension reacting to the event", "Simulation Extension OnEvent Contract Enforcement Interceptor"));
					throw new RuntimeException(contractResultForType.getMessage());
				}
				
				//TODO:: MANY 
				final ContractResult contractResultForCardinality = EventContractChecker.checkCardinality(resultEvent, onEvent);
				
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
