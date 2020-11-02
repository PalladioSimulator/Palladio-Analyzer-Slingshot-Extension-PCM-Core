package org.palladiosimulator.analyzer.slingshot.simulation.core.exceptions;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.annotations.EventContract;

public class EventContractException extends Exception {

	public EventContractException(final EventContract eventContract, final DESEvent event, final String reason) {
		super(String.format("The contract couldn't hold on event '%s' with EventContract('%s'). Reason: %s",
		        eventContract.toString(), event.getId(), reason));
	}

}
