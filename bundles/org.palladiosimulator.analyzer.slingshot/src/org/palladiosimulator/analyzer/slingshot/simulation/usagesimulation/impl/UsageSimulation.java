package org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl;

public interface UsageSimulation {
	
	
	SimulatedUser createSimulatedUser();
	
	// define current position of user within simulation
	void scheduleUserStartEvents();
	
}
