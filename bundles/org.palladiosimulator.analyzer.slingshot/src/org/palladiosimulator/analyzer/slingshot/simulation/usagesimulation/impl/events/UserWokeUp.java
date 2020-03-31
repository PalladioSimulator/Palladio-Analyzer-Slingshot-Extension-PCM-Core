package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import java.util.List;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.SimulatedUser;

public class UserWokeUp implements DESEvent {
	
	private final SimulatedUser user;
	private final String eventId;
	private final double timeToSleep;
	
	public UserWokeUp(final SimulatedUser user, final double timeToSleep) {
		this.eventId = UUID.randomUUID().toString();
		this.user = user;
		this.timeToSleep = timeToSleep;
	}
	

	@Override
	public String getId() {
		return null;
	}

	@Override
	public List<DESEvent> handle() {
		return List.of();
	}


	@Override
	public double getDelay() {
		return timeToSleep;
	}
	
	public SimulatedUser getSimulatedUser() {
		return user;
	}


	@Override
	public double time() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setTime(double time) {
		// TODO Auto-generated method stub
	}

}
 