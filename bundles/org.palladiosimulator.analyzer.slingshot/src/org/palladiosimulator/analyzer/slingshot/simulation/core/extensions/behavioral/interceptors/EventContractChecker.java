package org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral.interceptors;

import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral.annotations.EventCardinality;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral.annotations.OnEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.core.extensions.behavioral.results.ResultEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

/**
 * Helper class that provides methods for checking the specified contract. To
 * declare a extension contract, use {@link OnEvent}.
 * 
 * @author Julijan Katic
 */
public class EventContractChecker {

	/**
	 * Checks whether the cardinality applies as specified. It will return a failed
	 * ContractResult if the outputEvent provides more than one immediate event, but
	 * is specified with {@link EventCardinality.SINGLE} or vice versa. If the
	 * resulting event does not contain any immediate event at all, then it will
	 * return a successful contract result.
	 */
	public static ContractResult checkCardinality(final ResultEvent<DESEvent> outputEvent,
			final OnEvent onEventContract) {

		final String genericErrorMessage = "Extension Method Is Not Returning the Cardinality According to Contract, Returned:";
		ContractResult result = ContractResult.success();

		if (outputEvent.isOne()) {
			if (onEventContract.cardinality() != EventCardinality.SINGLE) {
				result = ContractResult.fail(
						genericErrorMessage + " Single Event, Contract: " + onEventContract.cardinality().toString());
			}
		} else if (outputEvent.areMany()) {
			if (!onEventContract.cardinality().equals(EventCardinality.MANY)) {
				result = ContractResult.fail(
						genericErrorMessage + " Many Events, Contract: " + onEventContract.cardinality().toString());
			}
		}

		return result;

	}

	/**
	 * Checks whether the result event returns the set of events as specified in the
	 * onEventContract. If not, a failed ContractResult is returned.
	 */
	static ContractResult checkEventType(final ResultEvent<DESEvent> outputResultEvent, final OnEvent onEventContract) {
		ContractResult result = ContractResult.success();

		final Class<? extends DESEvent>[] outputClazz = onEventContract.then();

		for (final DESEvent event : outputResultEvent.getEventsForScheduling()) {

			boolean eventTypeFound = false;

			for (final Class<? extends DESEvent> clazz : outputClazz) {
				if (clazz.isAssignableFrom(event.getClass())) {
					eventTypeFound = true;
					break;
				}
			}

			if (!eventTypeFound) {
				result = ContractResult.fail(String.format("Event type is not specified in the contract: '%s'",
						event.getClass().getSimpleName()));
				break;
			}
		}

		return result;
	}
}
