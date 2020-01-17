package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.ManyEvents;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.OptionalEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.results.SingleEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public class SimulationExtensionOnEventContractEnforcementInterceptor extends AbstractInterceptor {

	private final Logger LOGGER = Logger.getLogger(SimulationExtensionOnEventContractEnforcementInterceptor.class);

	public SimulationExtensionOnEventContractEnforcementInterceptor() {
	}

	@Override
	public void postIntercept(final Object extension, final Method m, final Object[] args, final Object result) {

		// 0. Annotations are present at all
		// 1. contracts are defined
		// 2. the cardinality of the returned result matches the contract definition
		
		// TODO:: Check what 

		if (!extension.getClass().getSuperclass().isAnnotationPresent(OnEvent.OnEvents.class)) {
			throw new RuntimeException("Extension does not provide any contract definition");
		}

		OnEvent.OnEvents annotations = extension.getClass().getSuperclass().getAnnotation(OnEvent.OnEvents.class);
		OnEvent[] onEvents = annotations.value();

		Class eventClass = args[0].getClass(); //SimulationStarted
		boolean annotationExists = false;

	
		for (OnEvent onEvent : onEvents) {
			if (onEvent.eventType().equals(eventClass)) {
				annotationExists = true;
				LOGGER.info("Annotation for the event type: " + onEvent.eventType().getName() + " exists");
				Class<? extends DESEvent> outputType = onEvent.outputEventType();

				if (!result.getClass().equals(outputType)) {

				}

				if (result instanceof ManyEvents) {
					if (!onEvent.cardinality().equals(EventCardinality.MANY)) {
						throw new RuntimeException(
								"Extension Method Is Not Returning the Cardinality According to Contract, Returned: Many Events, Contract: "
										+ onEvent.cardinality().toString());
					}

				} else if (result instanceof SingleEvent) {
					if (!onEvent.cardinality().equals(EventCardinality.SINGLE)) {
						throw new RuntimeException(
								"Extension Method Is Not Returning the Cardinality According to Contract, Returned: Single, Contract: "
										+ onEvent.cardinality().toString());
					}

				} else if (result instanceof OptionalEvent) {
					if (!onEvent.cardinality().equals(EventCardinality.SINGLE)) {
						throw new RuntimeException(
								"Extension Method Is Not Returning the Cardinality According to Contract, Returned: OptionalEvent with Cardinality Single, Contract: "
										+ onEvent.cardinality().toString());
					}
				}
				return;

			}

		}
		
		if (!annotationExists) {
			throw new RuntimeException("Extension Method does not provide a contract definition");
		}


	}

}
