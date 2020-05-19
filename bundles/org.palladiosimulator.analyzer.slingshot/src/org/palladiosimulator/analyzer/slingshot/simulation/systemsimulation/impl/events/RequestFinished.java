package org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl.events;

import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl.Request;

public class RequestFinished extends AbstractEntityChangedEvent<Request> {

	public RequestFinished(Request entity) {
		super(entity, 0);
		// TODO Auto-generated constructor stub
	}

}
