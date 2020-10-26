package org.palladiosimulator.analyzer.slingshot.behavior.usagesimulation;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;

public interface UsageSimulation {
	
	/**
	 * Retrieves the StartEvent for the given user.
	 */
	DESEvent findStartEvent(User user);
	
	/**
	 * Retrieve the next event for the given user.
	 */
	DESEvent findNextEvent(User user);
	
}
