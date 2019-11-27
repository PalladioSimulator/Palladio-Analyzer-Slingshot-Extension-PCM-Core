package org.palladiosimulator.analyzer.slingshot.simulation.events;

import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;

public class StartUserEvent implements DESEvent {
	
	private String eventId;
	private SimulatedUser simulatedUser;
	
	public SimulatedUser getSimulatedUser() {
		return simulatedUser;
	}
	
	public StartUserEvent(final SimulatedUser simulatedUser) {
		this.eventId = UUID.randomUUID().toString();
		this.simulatedUser = simulatedUser;
	}

	@Override
	public String getId() {
		return eventId;
	}

	@Override
	public void handle() {
		// notify listener of simualted user to identify and schedule the next event
		// or should overall simulation orchestrator be notified ?
		// FIXME: currently the next event are not scheduled
		// simulatedUser.nextEvent();
		// FIXME: in this handle there should be other events scheduled that change the state of the system and then the startUserEvent is finished
		// FIXME: However, there will not be a call to the interpreters so that it entangles effects for which this StartUserEvent is not responsible
		// FIXME: The semantic when the dispatcher propagates this event is that a StartUserEvent has been executed and the corresponding events that will
		// cause a chain of events have been scheduled. 
	}

}
