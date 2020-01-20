package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.events;

import java.util.List;
import java.util.UUID;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.*;

public class UserSlept implements DESEvent {

	private final SimulatedUser user;
	private final double timeToSleep;
	private final String eventId;
	private boolean handled = false;

	public UserSlept(final SimulatedUser user, final double timeToSleep) {
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
		if (!handled) {
			handled = true;
			UserWokeUp userWakeUpEvent = new UserWokeUp(user, timeToSleep);
			return List.of(userWakeUpEvent);
		}
		return List.of();
	}

	@Override
	public double getDelay() {
		return 0;
	}

}
