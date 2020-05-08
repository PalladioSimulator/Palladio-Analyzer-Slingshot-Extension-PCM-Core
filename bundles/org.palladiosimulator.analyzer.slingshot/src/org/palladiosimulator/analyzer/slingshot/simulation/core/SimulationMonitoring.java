package org.palladiosimulator.analyzer.slingshot.simulation.core;

import java.util.List;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.User;

public interface SimulationMonitoring {

	void addSimulatedUser();
	
	List<User> getSimulatedUsers();

}
