package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.EventPrettyLogPrinter;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.exceptions.NoContractDefinitionException;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.exceptions.ViolatedContractCardinalityException;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.exceptions.ViolatedContractTypeException;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.interceptor.AbstractInterceptor;

/**
 * This class post-intercepts the extension methods in order to enforce the
 * specified contract. This is done by checking the return types of the
 * extension method handler and whether its cardinality matches to the specified
 * cardinality in {@link OnEvent#cardinality()}.
 * 
 * @author Julijan Katic
 */
public class SimulationExtensionOnEventContractEnforcementInterceptor extends AbstractInterceptor {

	private final Logger LOGGER = Logger.getLogger(SimulationExtensionOnEventContractEnforcementInterceptor.class);

	/**
	 * @throws NoContractDefinitionException        if either the class does not
	 *                                              have any {@link OnEvent}
	 *                                              contract or the specified
	 *                                              contract does not belong to a
	 *                                              method.
	 * @throws ViolatedContractCardinalityException if the contract does not match
	 *                                              with the resulting cardinality
	 *                                              in {@link ResultEvent}.
	 * @throws ViolatedContractTypeException        if the resulting
	 *                                              {@link ResultEvent} contains an
	 *                                              instance of a type not specified
	 *                                              in the contract.
	 * @throws IllegalArgumentException             if the method has not exactly
	 *                                              one argument.
	 */
	@Override
	public void postIntercept(final Object extension, final Method m, final Object[] args, final Object result)
			throws NoContractDefinitionException, ViolatedContractCardinalityException, ViolatedContractTypeException {

		checkExtensionClass(extension);
		checkHasSingleArgument(args);

		final OnEvent.OnEvents annotations = extension.getClass().getSuperclass().getAnnotation(OnEvent.OnEvents.class);
		final OnEvent[] onEvents = annotations.value();

		final Class<?> eventClass = args[0].getClass();
		boolean annotationExists = false;

		final ResultEvent<DESEvent> resultEvent = (ResultEvent<DESEvent>) result;

		for (final OnEvent onEvent : onEvents) {
			if (onEvent.when().equals(eventClass)) {
				annotationExists = true;
				checkContractForResultType(args, resultEvent, onEvent);
				checkContractForCardinality(args, resultEvent, onEvent);
				break;
			}
		}

		if (!annotationExists) {
			LOGGER.info(EventPrettyLogPrinter.prettyPrint((DESEvent) args[0],
					"Enforcing contract on extension reacting to the event",
					"Simulation Extension OnEvent Contract Enforcement Interceptor"));
			throw new NoContractDefinitionException(eventClass.getCanonicalName());
		}

	}

	/**
	 * Helper method that checks whether args is not null and has exactly one
	 * argument.
	 * 
	 * @param args the argument (parameter) instance of the method.
	 * @throws IllegalArgumentException if args is null or has not exactly one
	 *                                  argument.
	 */
	private void checkHasSingleArgument(final Object[] args) throws IllegalArgumentException {
		if (args == null || args.length != 1) {
			throw new IllegalArgumentException("The behavior extension method handler must have exactly one argument.");
		}
	}

	/**
	 * Helper method for checking whether the contract are satisfied according to
	 * the cardinality.
	 * 
	 * @param args        The arguments (just for logging purposes).
	 * @param resultEvent The events returned by the method.
	 * @param onEvent     the contract itself.
	 * @throws ViolatedContractCardinalityException if the contract is violated.
	 */
	private void checkContractForCardinality(final Object[] args, final ResultEvent<DESEvent> resultEvent,
			final OnEvent onEvent) throws ViolatedContractCardinalityException {
		final ContractResult contractResultForCardinality = EventContractChecker.checkCardinality(resultEvent, onEvent);

		if (contractResultForCardinality.isFailed()) {
			LOGGER.info(EventPrettyLogPrinter.prettyPrint((DESEvent) args[0],
					"Enforcing contract on extension reacting to the event",
					"Simulation Extension OnEvent Contract Enforcement Interceptor"));
			throw new ViolatedContractCardinalityException(contractResultForCardinality.getMessage());
		}
	}

	/**
	 * Helper method for checking whether the contract are satisfied according to
	 * its type.
	 * 
	 * @param args        The arguments (just for logging purposes).
	 * @param resultEvent The events returned by the method.
	 * @param onEvent     the contract itself.
	 * 
	 * @throws ViolatedContractTypeException if the contract is violated regarding
	 *                                       its type.
	 */
	private void checkContractForResultType(final Object[] args, final ResultEvent<DESEvent> resultEvent,
			final OnEvent onEvent) throws ViolatedContractTypeException {
		final ContractResult contractResultForType = EventContractChecker.checkEventType(resultEvent, onEvent);

		if (contractResultForType.isFailed()) {
			LOGGER.info(EventPrettyLogPrinter.prettyPrint((DESEvent) args[0],
					"Failure of enforcing contract on extension reacting to the event",
					"Simulation Extension OnEvent Contract Enforcement Interceptor"));
			throw new ViolatedContractTypeException(contractResultForType.getMessage());
		}
	}

	/**
	 * Helper method for checking whether the class has any OnEvent annotation. Will
	 * throw {@link RuntimeException} otherwise.
	 * 
	 * @param extension the extension instance to check.
	 * @throws NoContractDefinitionException if the class does not have any contract
	 *                                       definition.
	 */
	private void checkExtensionClass(final Object extension) throws NoContractDefinitionException {
		if (!extension.getClass().getSuperclass().isAnnotationPresent(OnEvent.OnEvents.class)) {
			throw new NoContractDefinitionException(extension.getClass());
		}
	}

}
