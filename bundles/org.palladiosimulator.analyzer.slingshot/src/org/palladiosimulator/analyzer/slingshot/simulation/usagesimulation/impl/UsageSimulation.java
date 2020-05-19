package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

import org.palladiosimulator.analyzer.slingshot.simulation.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.User;

public interface UsageSimulation {
	
//	
//	/**
//	 * Identifies simulated users of the usage simulation
//	 * */
//	List<SimulatedUser> createSimulatedUsers();
	
	/**
	 * Retrieves the StartEvent for the given user
	 * */
	DESEvent findStartEvent(User user);
	
	/**
	 * Retrieves the next event for the given user
	 * */
	DESEvent findNextEvent(User user);
	
	
}
