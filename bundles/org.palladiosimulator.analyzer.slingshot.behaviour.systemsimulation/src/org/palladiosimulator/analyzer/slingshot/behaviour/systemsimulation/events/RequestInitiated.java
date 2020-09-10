package org.palladiosimulator.analyzer.slingshot.behaviour.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behaviour.systemsimulation.entities.Request;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class RequestInitiated extends AbstractEntityChangedEvent<Request> {

	public RequestInitiated(final Request entity, final double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
