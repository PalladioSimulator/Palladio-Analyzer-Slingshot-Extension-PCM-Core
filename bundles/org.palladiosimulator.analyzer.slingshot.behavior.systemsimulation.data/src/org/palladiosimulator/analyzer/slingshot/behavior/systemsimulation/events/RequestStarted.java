package org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behavior.systemsimulation.entities.Request;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;


public class RequestStarted extends AbstractEntityChangedEvent<Request> {

	public RequestStarted(final Request entity, final double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
