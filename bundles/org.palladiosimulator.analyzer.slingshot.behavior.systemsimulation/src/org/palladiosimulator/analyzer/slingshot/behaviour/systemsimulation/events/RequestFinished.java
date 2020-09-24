package org.palladiosimulator.analyzer.slingshot.behaviour.systemsimulation.events;

import org.palladiosimulator.analyzer.slingshot.behaviour.systemsimulation.entities.Request;
import org.palladiosimulator.analyzer.slingshot.simulation.events.AbstractEntityChangedEvent;

public class RequestFinished extends AbstractEntityChangedEvent<Request> {

	public RequestFinished(final Request entity) {
		super(entity, 0);
		// TODO Auto-generated constructor stub
	}

}
