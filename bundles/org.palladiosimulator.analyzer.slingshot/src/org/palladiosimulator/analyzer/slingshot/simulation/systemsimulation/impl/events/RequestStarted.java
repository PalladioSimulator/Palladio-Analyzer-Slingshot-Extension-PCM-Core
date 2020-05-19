package org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl.Request;

public class RequestStarted extends AbstractEntityChangedEvent<Request> {

	public RequestStarted(Request entity, double delay) {
		super(entity, delay);
		// TODO Auto-generated constructor stub
	}

}
