package org.palladiosimulator.analyzer.slingshot.behaviour.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behaviour.systemsimulation.Request;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;


public class RequestStarted extends AbstractEntityChangedEvent<Request> {

	public RequestStarted(final Request entity, final double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
